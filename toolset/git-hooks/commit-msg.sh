#!/usr/bin/env bash

cat $1

CHECK_MESSAGE=hook.commit-msg.check
if [ "`git config $CHECK_MESSAGE`" == "false" ]
then
    echo '\033[0;31m' checking message is disabled - to enable: '\033[0;37m'git config --unset $CHECK_MESSAGE '\033[0m'
else
    echo '\033[0;34m' checking message ... to disable: '\033[0;37m'git config --add $CHECK_MESSAGE false '\033[0m'

    # regex to validate in commit msg
    commit_regex='^\[HAR-[0-9]+]: '
    error_msg="Aborting commit. Your commit message is missing a JIRA Issue"

    if ! grep -iqE "$commit_regex" "$1"; then
        echo "$error_msg" >&2
        exit 1
    fi
fi
