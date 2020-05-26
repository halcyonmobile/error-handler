#!/bin/sh

last_commit_of_yesterday() {
    commit_since="yesterday.midnight"
    if [ "1" = "$(date +%u)" ] ; then
        commit_since="last friday.midnight"
    fi
    git log origin/develop --since="$commit_since" --pretty='%h' | head -1
}

last_commit_of_yesterday