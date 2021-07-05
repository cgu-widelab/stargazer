FROM php:7.2-apache
RUN apt-get update
RUN docker-php-ext-install pdo pdo_mysql mysqli

COPY . /var/www/html