#!/bin/sh

# Script to init wildfly in order to work with bargainhunt

# Get wildfly installation path
while [[ $# > 1 ]]
do
key="$1"

case ${key} in
    -w|--wildfly-home)
    WILDFLY_HOME="$2"
    shift # past argument
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

# try to locate standalone.xml
STANDALONE=${WILDFLY_HOME}/standalone/configuration/standalone.xml

if [ -w STANDALONE ]; then
    # Add datasource to standalone xml

    # Add postgres driver to standalone xml

#else
    echo "You need write access on standalone.xml\n"
    echo "Try \"sudo chmod -R g+w $(dirname ${STANDALONE})\""
    exit 1
fi

sudo chmod -R g+w /opt/wildfly/standalone/configuration