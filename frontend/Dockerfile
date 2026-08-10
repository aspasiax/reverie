# =========================================================
# Build stage
# =========================================================
FROM node:22-alpine AS build

WORKDIR /app

# The lockfile changes far less often than the source, so dependencies are
# installed in their own layer. 'npm ci' installs exactly the versions the
# lockfile records instead of resolving them again.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .

# Vite replaces this value at build time, not at run time, so it has to be
# known here. A single slash makes the client send its requests to its own
# origin, where nginx forwards everything under /api to the backend.
ARG VITE_API_URL=/
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# =========================================================
# Runtime stage
# =========================================================
# What ships is a web server and a folder of static files. Node is not part
# of it: the application was compiled to plain assets in the stage above.
FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80