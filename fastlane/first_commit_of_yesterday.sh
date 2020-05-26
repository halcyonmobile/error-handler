#!/bin/sh

first_commit_of_yesterday() {
    commit_since="yesterday.midnight"
    if [ "1" = "$(date +%u)" ] ; then
        commit_since="last friday.midnight"
    fi
    git log origin/develop --since="$commit_since" --pretty='%h' --reverse | head -1
}

first_commit_of_yesterday