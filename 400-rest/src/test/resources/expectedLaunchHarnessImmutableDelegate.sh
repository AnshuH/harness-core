#!/bin/bash -e
# Copyright 2021 Harness Inc. All rights reserved.
# Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
# that can be found in the licenses directory at the root of this repository, also available at
# https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.

sudo docker pull harness/delegate:latest

sudo docker run -d --restart unless-stopped --hostname="$(hostname -f | head -c 63)" \
-e ACCOUNT_ID=ACCOUNT_ID \
-e DELEGATE_TOKEN=ACCOUNT_KEY \
-e MANAGER_HOST_AND_PORT=https://localhost:9090 \
-e LOG_STREAMING_SERVICE_URL=http://localhost:8079 \
-e DELEGATE_NAME=harness-delegate \
-e DELEGATE_PROFILE=QFWin33JRlKWKBzpzE5A9A \
-e DELEGATE_TYPE=DOCKER \
-e DEPLOY_MODE=KUBERNETES \
-e PROXY_HOST= \
-e PROXY_PORT= \
-e PROXY_SCHEME= \
-e PROXY_USER= \
-e PROXY_PASSWORD= \
-e NO_PROXY= \
-e PROXY_MANAGER=true \
-e POLL_FOR_TASKS=false \
harness/delegate:latest