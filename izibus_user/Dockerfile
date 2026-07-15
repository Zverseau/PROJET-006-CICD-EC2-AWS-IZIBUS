FROM node:22-alpine as build-stage

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build-stage /app/dist/izibus_user/browser /usr/share/nginx/html

EXPOSE 80