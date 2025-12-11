# Stage 1: Build Angular App
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy all source files
COPY . .

# Build Angular for production
RUN npm run build -- --configuration production


# Stage 2: Serve via Nginx
FROM nginx:alpine

# Copy Angular build output
# Make sure 'eat-easy' is your Angular project name
COPY --from=build /app/dist/eat-easy/browser /usr/share/nginx/html

# Copy Nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
