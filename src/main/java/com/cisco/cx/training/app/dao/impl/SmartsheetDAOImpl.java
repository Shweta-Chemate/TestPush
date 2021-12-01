package com.cisco.cx.training.app.dao.impl;

import com.cisco.cx.training.app.config.PropertyConfiguration;
import com.cisco.cx.training.app.dao.SmartsheetDAO;
import com.cisco.cx.training.app.exception.NotFoundException;
import com.cisco.cx.training.models.SuccesstalkUserRegEsSchema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.SmartsheetFactory;
import com.smartsheet.api.models.Cell;
import com.smartsheet.api.models.Column;
import com.smartsheet.api.models.Row;
import com.smartsheet.api.models.Sheet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SmartsheetDAOImpl implements SmartsheetDAO {
    private final static Logger LOG = LoggerFactory.getLogger(SmartsheetDAOImpl.class);
    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PropertyConfiguration config;
    
    private static final String eventNameKey = "Event Name";
    private static final String emailKey = "Email";
    private static final String registeredKey = "Registered";

    private static Map<String, String> successTalkRegColTitleToSchemaKeyMap = Stream.of(new Object[][] {
            { eventNameKey, "title" },
            { emailKey, "email" },
            { registeredKey, "registrationStatus" },
            { "Attended", "attendedStatus" },
            { "Registration Date/Time", "registrationDateFormatted"},
            { "Event Start Date", "eventStartDateFormatted"}
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));

    @Override
	public boolean checkRegistrationExists(SuccesstalkUserRegEsSchema registration) throws SmartsheetException {
        // get a hook to the smartsheet sheet
        Smartsheet client = this.createClient();
        Sheet successTalkRegistrationSheet = this.getSuccessTalkRegistrationSmartsheet(client);

        // form a map of smartsheet column ids to column titles
        Map<Long, String> columnIdToTitleMap = new HashMap<>();
        successTalkRegistrationSheet.getColumns().forEach(column -> {
            columnIdToTitleMap.put(column.getId(), column.getTitle());
        });

        return (null != this.searchRow(registration, successTalkRegistrationSheet, columnIdToTitleMap,
                Arrays.asList(
                		SuccesstalkUserRegEsSchema.RegistrationStatusEnum.PENDING.toString(),
                		SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED.toString()
                )
        ));
    }

    @Override
	public void saveSuccessTalkRegistration(SuccesstalkUserRegEsSchema registration) throws SmartsheetException {
        // get a hook to the smartsheet sheet
        Smartsheet client = this.createClient();
        // try to find a matching cancelled smartsheet row for the registration if it exists
        Row previouslyCanceledRegistration = this.updateExistingSuccessTalkRegistration(registration,
                Arrays.asList(
                		SuccesstalkUserRegEsSchema.RegistrationStatusEnum.CANCELLED.toString()
                ));

        if (previouslyCanceledRegistration == null) {
            // add a new row to the smartsheet
            client.sheetResources().rowResources().addRows(config.getSuccessTalkRegistrationSheetId(),
                    Arrays.asList(createRow(registration, this.getSuccessTalkRegistrationSmartsheet(client).getColumns())));
        }
    }

    @Override
	public void cancelUserSuccessTalkRegistration(SuccesstalkUserRegEsSchema cancelledRegistration) throws SmartsheetException {
        // try to find the registration smartsheet row
        Row existingSuccessTalkRegistration = this.updateExistingSuccessTalkRegistration(cancelledRegistration,
                        Arrays.asList(
                        		SuccesstalkUserRegEsSchema.RegistrationStatusEnum.PENDING.toString(),
                        		SuccesstalkUserRegEsSchema.RegistrationStatusEnum.REGISTERED.toString()
                        ));

        if (existingSuccessTalkRegistration == null) {
            // if there is no existingSuccessTalkRegistration available, return error because this record is not found in the smartsheet
            LOG.error("Could not find this registration in Smartsheet - title: {},ccoid: {}",
                                cancelledRegistration.getTitle(), cancelledRegistration.getCcoid());

            throw new NotFoundException("Could not find an active registration in Smartsheet for title: " + cancelledRegistration.getTitle()
                                        + ", ccoid: " + cancelledRegistration.getCcoid());
        }
    }

    private Row updateExistingSuccessTalkRegistration(SuccesstalkUserRegEsSchema cancelledRegistration, List<String> allowedStatuses) throws SmartsheetException {
        // get a hook to the smartsheet sheet
        Smartsheet client = this.createClient();
        Sheet successTalkRegistrationSheet = this.getSuccessTalkRegistrationSmartsheet(client);

        // form a map of smartsheet column ids to column titles
        Map<Long, String> columnIdToTitleMap = new HashMap<>();
        successTalkRegistrationSheet.getColumns().forEach(column -> {
            columnIdToTitleMap.put(column.getId(), column.getTitle());
        });

        // find the matching smartsheet row for the registration
        Row matchedRow = this.searchRow(cancelledRegistration, successTalkRegistrationSheet, columnIdToTitleMap, allowedStatuses);

        if (matchedRow != null) {
            // if row is a match, create a new row object for update
            Row updatedMatchedRow = new Row(matchedRow.getId());
            // only allow updates to columns defined in the map
            Set<String> allowedColumns = successTalkRegColTitleToSchemaKeyMap.keySet();

            // set cells to the new row object and -
            // filter out the columns which are not in the allowed map
            // map the filtered cell to set the transaction type to Canceled (set in the input object)
            updatedMatchedRow.setCells(matchedRow.getCells().stream()
                    .filter(cell -> allowedColumns.contains(columnIdToTitleMap.get(cell.getColumnId())))
                    .map(cell -> {
                        if (StringUtils.equalsIgnoreCase(columnIdToTitleMap.get(cell.getColumnId()), registeredKey)) {
                            cell.setValue(cancelledRegistration.getRegistrationStatus());
                            cell.setDisplayValue(cancelledRegistration.getRegistrationStatus().toString());
                        } else if (StringUtils.equalsIgnoreCase(columnIdToTitleMap.get(cell.getColumnId()), emailKey)) {
                            cell.setValue(cancelledRegistration.getCcoid());
                            cell.setDisplayValue(cancelledRegistration.getCcoid());
                        } else if (StringUtils.equalsIgnoreCase(columnIdToTitleMap.get(cell.getColumnId()), eventNameKey)) {
                            cell.setValue(cancelledRegistration.getTitle());
                            cell.setDisplayValue(cancelledRegistration.getTitle());
                        }

                        return cell;
                    })
                    .filter(cell -> StringUtils.isNotBlank(cell.getDisplayValue()))
                    .collect(Collectors.toList()));

            // update the row in the smartsheet
            client.sheetResources().rowResources().updateRows(config.getSuccessTalkRegistrationSheetId(), Arrays.asList(updatedMatchedRow));
        }

        return matchedRow;
    }

    private Row searchRow(SuccesstalkUserRegEsSchema registration, Sheet successTalkRegistrationSheet, Map<Long, String> columnIdToTitleMap, List<String> status) {
        // reduce the list of rows to just a matched row for the input field values - event name and user email
        return successTalkRegistrationSheet.getRows().stream().reduce(null, (runningMatchedRow, currentRow) -> {
            if (runningMatchedRow == null) {
                // map the columns to a flag indicating if the row is a match based on the cell value,
                // then reduce the column flags to a boolean value indicating if the row is a match
                boolean isRowMatch = currentRow.getCells().stream().map(currentCell -> {
                    // start with flag true (at start every cell is qualified for row to be a match)
                    boolean isCellQualified = true;
                    //LOG.info("{} -> {}, {}", currentCell.getColumnId(), currentCell.getValue(), currentCell.getDisplayValue());
                    // check for field conditions and determine if the cell values qualifies or disqualifies the row to be matched
                    if (StringUtils.equalsIgnoreCase(columnIdToTitleMap.get(currentCell.getColumnId()), eventNameKey)) {
                        isCellQualified = isCellQualified && (Objects.equals(registration.getTitle(), currentCell.getValue()));
                    } else if (StringUtils.equalsIgnoreCase(columnIdToTitleMap.get(currentCell.getColumnId()), emailKey)) {
                        isCellQualified = isCellQualified && (Objects.equals(registration.getCcoid(), currentCell.getValue()));
                    } else if (StringUtils.equalsIgnoreCase(columnIdToTitleMap.get(currentCell.getColumnId()), registeredKey)) {
                        isCellQualified = isCellQualified && (status == null || status.isEmpty() || status.contains(currentCell.getValue()));
                    }

                    return isCellQualified;
                }).reduce((rowMatched, cellMatched) -> rowMatched && cellMatched).get();

                // if row is match, return currentRow else return null
                return isRowMatch ? currentRow : null;
            } else {
                // once a row is matched, shortcircuit rest of row reduce by returning the matchedRow through
                return runningMatchedRow;
            }
        });
    }

    private Smartsheet createClient() {
        return SmartsheetFactory.createDefaultClient(config.getSmartsheetAccessToken());
    }

    private Sheet getSuccessTalkRegistrationSmartsheet(Smartsheet client) throws SmartsheetException {
        return client.sheetResources().getSheet(
                config.getSuccessTalkRegistrationSheetId(), // long sheetId
                null,                      // EnumSet<SheetInclusion> includes
                null,                     // EnumSet<ObjectExclusion> excludes
                null,                          // Set<Long> rowIds
                null,                         // Set<Integer> rowNumbers
                null,                         // Set<Long> columnIds
                null,                       // Integer pageSize
                null                       // Integer page
        );
    }

    @SuppressWarnings("unchecked")
	private Row createRow(SuccesstalkUserRegEsSchema registration, List<Column> columns) {
        Row registrationRow = new Row();
        registrationRow.setCells(this.constructCells(mapper.convertValue(registration, Map.class), columns)).setToBottom(true);
        return registrationRow;
    }

    private List<Cell> constructCells(Map<String, Object> model, List<Column> columns) {
        List<Cell> cells = new ArrayList<>();
        LOG.info("Cell Values {}", model);
        columns.forEach(column -> {
            Object cellValue = model.get((successTalkRegColTitleToSchemaKeyMap != null ? successTalkRegColTitleToSchemaKeyMap.get(column.getTitle()) : column.getTitle()));
            if (cellValue != null) {
                cells.add(new Cell(column.getId()).setValue(cellValue).setDisplayValue(cellValue.toString()));
            }
        });

        LOG.info("Cells {}", cells);
        return cells;
    }
}
