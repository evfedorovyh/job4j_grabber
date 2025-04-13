CREATE TABLE IF NOT EXISTS post (
id SERIAL PRIMARY KEY,
title text,
link text UNIQUE,
description text,
time timestamp);