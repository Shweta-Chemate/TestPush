## Do not execute this script if you dont know what you are doing
## This will modify current directory and files to setup a new springboot project

# Check if project-name is passed
[ -z "$1" ] && {
  # Print usage and pre-requisites
  echo "Usage: $0 <your-project-name>"
  echo "Warning: This will modify the current project directory permanently."
  exit 1
}

export LC_CTYPE=C
# Replace the occurences of template name with the input project name
find . -type f \( -name "*" ! -name "*.sh" \) -print0 | xargs -0 sed -i '' -e "s/springboot-template/$1/g"

# Replace the README.md with README-welcome.md
mv -f README-welcome.md README.md

# Refactor the chart dir name
mv deploy/charts/springboot-template "deploy/charts/$1"

# Remove the existing .git folder
rm -rf .git

# Init a new git repo
git init
# Do an initial commit
git add .
git commit -m "[autogen] Initial Version for $1"

echo "Current directory has been converted into project $1"
