#!/usr/bin/env sh

## Purpose: Adds label lines to an existing Dockerfile. Uses git config to detect project details.
## Usage:   Run this script from the root of your project checkout and push the result to git.
## Support: by amimitt2, santnair

#isDebug=1                           ## Comment this line to suppress debug messages.
fileName="Dockerfile"
tempName="Dockerfile_temp"

## 1. Get the line number of the pattern line
lineNum=$(grep -n -e "^FROM .*" $fileName | cut -d: -f1 )
test -n "$isDebug" && printf "Found match for 'FROM' in %s on lineNum=%s\n" "${fileName}" "$lineNum"

## 2. Copy the lines including the above lineNum into a new file
printf "Writing lines from %s upto lineNum=%s into %s\n" "${fileName}" "${lineNum}" "${tempName}"
head -n "$lineNum" $fileName >> ${tempName}

## 3. Get project details from git and hard-code the rest
test -n "$isDebug" && printf "Fetching some values from 'git config --local'\n"
projectOrg="Cisco CXE"
projectDesc="CHANGEME"
projectUrl=$(git config --local remote.origin.url )
projectName=$(git config --local remote.origin.url | sed -n 's#.*/\([^.]*\)\.git#\1#p' )

test -n "$isDebug" && printf "ProjectUrl  = %s\n" "${projectUrl}"
test -n "$isDebug" && printf "ProjectName = %s\n" "${projectName}"
test -n "$isDebug" && printf "ProjectOrg  = %s\n" "${projectOrg}"
test -n "$isDebug" && printf "ProjectDesc = %s\n" "${projectDesc}"

## 3. Insert label lines into the new file and expand variables
printf "Writing lines for label after lineNum=%s into %s\n" "${lineNum}" "${tempName}"
cat << EOL >>${tempName}

LABEL org.opencontainers.image.title="${projectName}" \\
      org.opencontainers.image.description="${projectDesc}" \\
      org.opencontainers.image.url="${projectUrl}" \\
      org.opencontainers.image.source="${projectUrl}" \\
      org.opencontainers.image.vendor="${projectOrg}" \\
      org.opencontainers.image.revision="\$VCS_REF" \\
      org.opencontainers.image.created="\$BUILD_DATE" \\
      org.label-schema.schema-version="1.0" \\
      org.label-schema.name="${projectName}" \\
      org.label-schema.description="${projectDesc}" \\
      org.label-schema.url="${projectUrl}" \\
      org.label-schema.vcs-url="${projectUrl}" \\
      org.label-schema.vendor="${projectOrg}" \\
      org.label-schema.vcs-ref="\$VCS_REF" \\
      org.label-schema.build-date="\$BUILD_DATE"
EOL

## 4. Copy remainder of the file after the pattern line
printf "Writing lines from %s after lineNum=%s into %s\n" "${fileName}" "${lineNum}" "${tempName}"
tail -n +$((lineNum + 1)) $fileName >> ${tempName}

## 5. Replace original file
test -n "$isDebug" && printf "Replace %s by the updated %s\n" "${fileName}" "${tempName}"
mv ${tempName} $fileName

printf "Please review the updated %s and commit/push it into the remote repo\n" "${Dockerfile}"
printf "All done!\n"

## EOF
