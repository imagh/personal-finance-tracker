# usage: check-retry.sh <worktree> <tag> [gradle args...]
# Runs `./gradlew check` (plus any extra args, e.g. --no-build-cache) in <worktree>. At the first
# AppNotIdleException it kills only this Gradle client and reruns, up to 3 attempts (the known
# Robolectric hang, docs/learnings/DIGEST.md). Never run `./gradlew --stop`.
# Run it in the background and watch its `=== ` lines; each attempt's full log is
# $PFT_LOG_DIR/check-<tag>-<n>.log (default /tmp/pft-briefs).
export PATH="/opt/homebrew/bin:$PATH"
logdir=${PFT_LOG_DIR:-/tmp/pft-briefs}
mkdir -p "$logdir"
wt=$1; tag=$2; shift 2
cd "$wt" || exit 1
for attempt in 1 2 3; do
  log=$logdir/check-$tag-$attempt.log
  echo "=== $tag attempt $attempt start $(date +%T)"
  ./gradlew check "$@" > "$log" 2>&1 &
  pid=$!; hung=0
  while kill -0 $pid 2>/dev/null; do
    if grep -q AppNotIdleException "$log"; then hung=1; kill $pid; wait $pid 2>/dev/null; break; fi
    sleep 5
  done
  if [ $hung -eq 1 ]; then echo "=== $tag attempt $attempt HANG $(grep -B1 'AppNotIdleException at' "$log" | grep -m1 'Test >' | cut -c1-80) $(date +%T)"; sleep 10; continue; fi
  wait $pid; rc=$?
  echo "=== $tag attempt $attempt exit=$rc $(date +%T)"; exit $rc
done
echo "=== $tag gave up after 3 hangs"; exit 1
