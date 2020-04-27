##----------------------------------------------------------------------------------------------------------------------------------------------------
## Purpose: Integration with CIDER toolkit provides Continuous Integration & Delivery of service(s) to any Kubernetes cluster using simple commands.
##          Also intented to be useful to developers during local development and deployment to minikube or a local k8s cluster.
## Depends: cider-toolkit (in CREANDO git repo)
##----------------------------------------------------------------------------------------------------------------------------------------------------


## ----------------------------------------------------------------------------
## See "MAKEFILE STYLE GUIDE" http://clarkgrubb.com/makefile-style-guide
## ----------------------------------------------------------------------------
MAKEFLAGS += --warn-undefined-variables
SHELL := bash
.SHELLFLAGS := -eu -o pipefail -c
.DEFAULT_GOAL := showUsage
.DELETE_ON_ERROR:
.SUFFIXES:


## ----------------------------------------------------------------------------
## CIDER variables begin
## ----------------------------------------------------------------------------

## CIDER requires these variables, unless stated as 'optional'.
## You can change values to match your local env, but do not push your changes to the git repo.

## ---------------------------------------------------------------------------------------------------
## For deployments to your personal kubernetes cluster (Minikube, Docker for Mac, Docker for Windows):
## ---------------------------------------------------------------------------------------------------
##  - Developer must git clone the cider-toolkit from CREANDO repo.
##  - Set an EnvVar in your .profile, .bash_profile, or .bashrc like this: 'export CIDER_DIR=${HOME}/changeThisPath/cider-toolkit'
##  - Provide the kubeconfig file using --kubeconfig or set up the KUBECONFIG EnvVar to point to a different config file.

## CIDER dir is the absolute path to the cider-toolkit. CI sets this path automatically. If developers wish to use CIDER, see instructions above.

## Chart dir is the absolute path from this Makefile to the chart dir of this project.
export CHART_DIR="${PWD}/helm-chart"

## Release name. Release name of this project. Used by Helm to perform the deployment.
export RELEASE_NAME="cxpp-training-enablement"

## Team dir is optional. Very few teams are expected to have team-specific configs. If your team doesn't have a config, leave this blank.
export TEAM_DIR=""

## During Continuous Integration on the CI build server, if event is 'push' (not 'tag), perform Continous Delivery to this target environment.
## Set your Continuous Delivery target carefully to deploy your service to the expected environment during 'push'. CD is skipped if value is empty.
## During local development, CD will not occur, regardless of the value set below.
export CD_TARGET="p3__idev"  ## p3__idev is mapped to sdp11-idev.csco.cloud; see CIDER documentation on coordinates.

## CIDER operations require a tag in a specific format. Drone gets the tag from BitBucket(git) and exposes it as CI_COMMIT_REF.
## For local development and local deployment (minikube), developers can switch the commit ref, but do not push your changes to git.

## During Continuous Integration on the CI build server, the commit ref is ${CI_COMMIT_REF}
## During local development, the developer can set their own version to imitate a drone build env. Do not push this to git uncommented.
export t="${CI_COMMIT_REF}"

## ----------------------------------------------------------------------------
## CIDER variables ended
## ----------------------------------------------------------------------------


## ----------------------------------------------------------------------------
## CIDER targets begin
## ----------------------------------------------------------------------------

.PHONY: showVars
showVars:
	@printf "CIDER requires these variables to be exported:\n"
	@printf "\tt=$(t)\n"
	@printf "\tCIDER_DIR=$(CIDER_DIR)\n"
	@printf "\tCHART_DIR=$(CHART_DIR)\n"
	@printf "\tTEAM_DIR=$(TEAM_DIR)\n"
	@printf "\tRELEASE_NAME=$(RELEASE_NAME)\n"
	@printf "\nNOTE: Team dir is optional.\n\n"

.PHONY: showUsage
showUsage: showVars
	$(MAKE) -C $(CIDER_DIR) showUsage

.PHONY: clean
clean: showVars
	$(MAKE) -C $(CIDER_DIR) clean ;\
	CIDER_DIR=$(CIDER_DIR) ;\
	CHART_DIR=$(CHART_DIR)

.PHONY: deploy
deploy: showVars
	$(MAKE) -C $(CIDER_DIR) deploy ;\
	t=$(t) ;\
	CIDER_DIR=$(CIDER_DIR) ;\
	CHART_DIR=$(CHART_DIR) ;\
	TEAM_DIR=$(TEAM_DIR) ;\
	RELEASE_NAME=$(RELEASE_NAME) ;\
	CD_TARGET=$(CD_TARGET)

## ----------------------------------------------------------------------------
## CIDER targets ended
## ----------------------------------------------------------------------------


## EOF