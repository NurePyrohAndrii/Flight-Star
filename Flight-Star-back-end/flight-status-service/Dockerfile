# Stage 1: Base Image
FROM node:18-alpine as base
WORKDIR /app

# Stage 2: Production Dependencies
FROM base as production-dependencies
COPY package.json package-lock.json ./
RUN npm config set fetch-retries 5 \
    && npm config set fetch-retry-mintimeout 20000 \
    && npm config set fetch-retry-maxtimeout 120000 \
    && npm ci --production

# Stage 3: Build Dependencies
FROM base as build-dependencies
COPY package.json package-lock.json ./
RUN npm config set fetch-retries 5 \
    && npm config set fetch-retry-mintimeout 20000 \
    && npm config set fetch-retry-maxtimeout 120000 \
    && npm ci

# Stage 4: Builder
FROM build-dependencies as builder
COPY . .
RUN npm run build

# Stage 5: Final Image
FROM node:18-alpine as final
WORKDIR /app
COPY --from=production-dependencies /app .
COPY --from=builder /app/dist /app/dist
CMD [ "node", "dist/index.js" ]
