#!/usr/bin/env bash

source ./utils.sh

INSTALLER_DIR=harness_installer
MANAGER_DIR=$INSTALLER_DIR/manager
VERIFICATION_DIR=$INSTALLER_DIR/verification
UI_DIR=$INSTALLER_DIR/ui
LE_DIR=$INSTALLER_DIR/le
STORAGE_DIR=$INSTALLER_DIR/storage
jre_version=8u131

rm -rf $INSTALLER_DIR

mkdir -p $INSTALLER_DIR

mkdir $MANAGER_DIR
mkdir $VERIFICATION_DIR
mkdir $UI_DIR
mkdir $STORAGE_DIR
mkdir $LE_DIR


cp -R scripts $INSTALLER_DIR/
cp -R config $INSTALLER_DIR/
cp *.properties $INSTALLER_DIR
cp install_harness.sh $INSTALLER_DIR
cp utils.sh $INSTALLER_DIR
cp stop.sh $INSTALLER_DIR
chmod +x $INSTALLER_DIR/*.sh
CONFIG_PROPERTIES_FILE=$INSTALLER_DIR/config.properties
cp -R splunk_pyml $LE_DIR/

if [[ -z $1 ]]; then
   echo "No license file supplied, skipping setting the license file in the installer"
else
   echo "License file supplied, generating installer with license file $1"
   replace harness_license $1 ${CONFIG_PROPERTIES_FILE} $INSTALLER_DIR
fi

JRE_SOURCE_URL=https://app.harness.io/storage/wingsdelegates/jre/8u191
JRE_SOLARIS=jre-8u191-solaris-x64.tar.gz
JRE_MACOSX=jre-8u191-macosx-x64.tar.gz
JRE_LINUX=jre-8u191-linux-x64.tar.gz
JRE_LINUX_DIR=jre1.8.0_191/

curl "${JRE_SOURCE_URL}/${JRE_SOLARIS}" > "${JRE_SOLARIS}"
curl "${JRE_SOURCE_URL}/${JRE_MACOSX}" > "${JRE_MACOSX}"
curl "${JRE_SOURCE_URL}/${JRE_LINUX}" > "${JRE_LINUX}"


function setupDelegateJars(){
   echo "################################Setting up Delegate Jars ################################"

    DELEGATE_VERSION=$(getProperty "version.properties" "DELEGATE_VERSION")
    WATCHER_VERSION=$(getProperty "version.properties" "WATCHER_VERSION")

    mkdir -p $STORAGE_DIR/wingsdelegates/jre/8u191/

    cp jre-8u191-solaris-x64.tar.gz $STORAGE_DIR/wingsdelegates/jre/8u191/
    cp jre-8u191-macosx-x64.tar.gz $STORAGE_DIR/wingsdelegates/jre/8u191/
    cp jre-8u191-linux-x64.tar.gz $STORAGE_DIR/wingsdelegates/jre/8u191/

    rm -rf ${STORAGE_DIR}/wingsdelegates/jobs/deploy-prod-delegate/*
    mkdir -p  ${STORAGE_DIR}/wingsdelegates/jobs/deploy-prod-delegate/${DELEGATE_VERSION}
    cp delegate.jar ${STORAGE_DIR}/wingsdelegates/jobs/deploy-prod-delegate/${DELEGATE_VERSION}/

    echo "1.0.${DELEGATE_VERSION} jobs/deploy-prod-delegate/${DELEGATE_VERSION}/delegate.jar" > delegateprod.txt

    mv delegateprod.txt ${STORAGE_DIR}/wingsdelegates

    rm -rf ${STORAGE_DIR}/wingswatchers/jobs/deploy-prod-watcher/*
    mkdir -p  ${STORAGE_DIR}/wingswatchers/jobs/deploy-prod-watcher/${WATCHER_VERSION}
    cp watcher.jar ${STORAGE_DIR}/wingswatchers/jobs/deploy-prod-watcher/${WATCHER_VERSION}/
    echo "1.0.${WATCHER_VERSION} jobs/deploy-prod-watcher/${WATCHER_VERSION}/watcher.jar" > watcherprod.txt
    mv watcherprod.txt ${STORAGE_DIR}/wingswatchers

    for platform in linux darwin; do

      for version in v1.13.2; do

        echo "Copying kubectl ${version} binaries for ${platform}"

        sudo mkdir -p ${STORAGE_DIR}/harness-download/kubernetes-release/release/${version}/bin/${platform}/amd64/

        curl  -s -L -o kubectl https://app.harness.io/storage/harness-download/kubernetes-release/release/${version}/bin/${platform}/amd64/kubectl

        echo $(ls -sh kubectl  | cut -d ' ' -f1)

        sudo cp kubectl ${STORAGE_DIR}/harness-download/kubernetes-release/release/${version}/bin/${platform}/amd64/

      done


      for version in v0.2 v0.3; do

        echo "Copying go-template  ${version} binaries for ${platform}"

        sudo mkdir -p ${STORAGE_DIR}/harness-download/snapshot-go-template/release/${version}/bin/${platform}/amd64/

        curl  -s -L -o go-template https://app.harness.io/storage/harness-download/snapshot-go-template/release/${version}/bin/${platform}/amd64/go-template

        echo $(ls -sh go-template  | cut -d ' ' -f1)

        sudo cp go-template ${STORAGE_DIR}/harness-download/snapshot-go-template/release/${version}/bin/${platform}/amd64/

      done


      for version in v2.13.1 v3.0.2; do

        echo "Copying helm ${version} binaries for ${platform}"

        sudo mkdir -p ${STORAGE_DIR}/harness-download/harness-helm/release/${version}/bin/${platform}/amd64/

        curl  -s -L -o helm https://app.harness.io/storage/harness-download/harness-helm/release/${version}/bin/${platform}/amd64/helm

        echo $(ls -sh helm  | cut -d ' ' -f1)

        sudo cp helm ${STORAGE_DIR}/harness-download/harness-helm/release/${version}/bin/${platform}/amd64/

      done


      for version in v0.8.2; do

        echo "Copying chartmuseum ${version} binaries for ${platform}"

        sudo mkdir -p ${STORAGE_DIR}/harness-download/harness-chartmuseum/release/${version}/bin/${platform}/amd64/

        curl  -s -L -o chartmuseum https://app.harness.io/storage/harness-download/harness-chartmuseum/release/${version}/bin/${platform}/amd64/chartmuseum

        echo $(ls -sh chartmuseum  | cut -d ' ' -f1)

        sudo cp chartmuseum ${STORAGE_DIR}/harness-download/harness-chartmuseum/release/${version}/bin/${platform}/amd64/

      done

      for version in v3.5.4; do

        echo "Copying kustomize ${version} binaries for ${platform}"

        sudo mkdir -p ${STORAGE_DIR}/harness-download/harness-kustomize/release/${version}/bin/${platform}/amd64/

        curl  -s -L -o kustomize https://app.harness.io/storage/harness-download/harness-kustomize/release/${version}/bin/${platform}/amd64/kustomize

        echo $(ls -sh kustomize  | cut -d ' ' -f1)

        sudo cp kustomize ${STORAGE_DIR}/harness-download/harness-kustomize/release/${version}/bin/${platform}/amd64/

      done

      for version in v1.0; do

        echo "Copying terraform-config-inspect v${version} binaries for ${platform}"

        sudo mkdir -p ${STORAGE_DIR}/harness-download/harness-terraform-config-inspect/${version}/${platform}/amd64/

        curl  -s -L -o terraform-config-inspect https://app.harness.io/storage/harness-download/harness-terraform-config-inspect/${version}/${platform}/amd64/terraform-config-inspect

        echo $(ls -sh terraform-config-inspect  | cut -d ' ' -f1)

        sudo cp terraform-config-inspect ${STORAGE_DIR}/harness-download/harness-terraform-config-inspect/${version}/${platform}/amd64/

      done

      for version in v4.2.16; do

        echo "Copying oc ${version} binaries for ${platform}"

        sudo mkdir -p ${STORAGE_DIR}/harness-download/harness-oc/release/${version}/bin/${platform}/amd64/

        curl  -s -L -o oc https://app.harness.io/storage/harness-download/harness-oc/release/${version}/bin/${platform}/amd64/oc

        echo $(ls -sh oc  | cut -d ' ' -f1)

        sudo cp oc ${STORAGE_DIR}/harness-download/harness-oc/release/${version}/bin/${platform}/amd64/

      done

    done
}


function setUpArtifacts(){
   echo "################################ Preparing artifacts in their respective folders  ################################"
   cp rest-capsule.jar $MANAGER_DIR
   cp config.yml $MANAGER_DIR

   cp verification-capsule.jar $VERIFICATION_DIR
   cp verification-config.yml $VERIFICATION_DIR

   cp -R static $UI_DIR/

   setupDelegateJars
}

function setUpJRE(){
    cp $JRE_LINUX $INSTALLER_DIR
    cd $INSTALLER_DIR
    tar -xvf $JRE_LINUX
    rm -f $JRE_LINUX
    ln -s $JRE_LINUX_DIR jre
    cd -
}


setUpJRE
setUpArtifacts
tar -cvzf harness_installer.tar.gz $INSTALLER_DIR