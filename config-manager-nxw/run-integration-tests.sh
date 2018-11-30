#!/bin/bash
pushd "$(dirname "${BASH_SOURCE[0]}")" || exit 1

pushd src/test/integration || exit 1

for i in *.sh; do
./"$i"
done

popd
popd