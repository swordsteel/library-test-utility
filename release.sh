#!/bin/sh

setup() {
    git fetch
    git checkout develop
}

check_last_commit() {
    last_commit_message=$(git log -1 --pretty=format:%s)
    if [ "$last_commit_message" = "[RELEASE] - bump version" ]; then
        echo "Nothing to release!!!"
        exit 1
    fi
}

un_snapshot_version() {
    sed -i 's/\(version\s*=\s*[0-9.]*\)-SNAPSHOT/\1/' gradle.properties
}

get_current_version() {
    echo "$(awk -F '=' '/version\s*=\s*[0-9.]*/ {gsub(/^ +| +$/,"",$2); print $2}' gradle.properties)"
}

commit_change() {
    git commit -m "[RELEASE] - $1" gradle.properties
    git push --porcelain origin develop
}

add_tag() {
    gitTag="v$(get_current_version)"
    git tag -a "$gitTag" -m "release"
    git push --porcelain origin "$gitTag"
}

merge_into_main() {
    git checkout main
    git merge develop -m "release version '$(get_current_version)'" --ff-only
    git push --porcelain origin main
    git checkout develop
    git rebase origin/main
}

snapshot_version() {
    new_version=$(echo $(get_current_version) | awk -F '.' '{print $1 "." $2 "." $3+1}')
    sed -i "s/\(version\s*=\s*\)[0-9.]*/\1$new_version-SNAPSHOT/" gradle.properties
}

# gitCmd4Azure
setup

# checkLastCommit
check_last_commit

# unSnapshotVersion
un_snapshot_version

# commitVersionChange
commit_change "release version: $(get_current_version)"

# preTagCommit
add_tag

# something
merge_into_main

# preTagCommit
snapshot_version

# commitVersionChange
commit_change 'bump version'
