# usage: check-commits.sh <worktree> <tag> <sha>...
# Checks each commit in turn with check-retry.sh, in a temporary detached worktree
# (`git worktree add --detach <path> <first-sha>`, removed afterwards). Stops at the first real failure.
# Trust the `=== ` lines: the loop's own exit code can read 0 when piped.
here=$(cd "$(dirname "$0")" && pwd)
wt=$1; tag=$2; shift 2
for sha in "$@"; do
  git -C "$wt" checkout -q --detach "$sha" || { echo "=== $tag checkout $sha failed"; exit 1; }
  echo "=== $tag commit $sha $(git -C "$wt" log -1 --format=%s)"
  bash "$here/check-retry.sh" "$wt" "$tag-$sha"; rc=$?
  if [ $rc -ne 0 ]; then echo "=== $tag commit $sha FAILED rc=$rc"; exit $rc; fi
done
echo "=== $tag all commits green"
