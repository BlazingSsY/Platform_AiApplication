# syntax=docker/dockerfile:1
FROM node:20-alpine AS build

WORKDIR /app
COPY portal/package*.json ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci

COPY portal/ ./
RUN npm run build-prod

FROM nginx:1.27-alpine

COPY docker/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
