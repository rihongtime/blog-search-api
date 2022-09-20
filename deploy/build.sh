#!/usr/bin/env bash

eval "cd .."
eval "./gradlew :application:api:clean build -x test"