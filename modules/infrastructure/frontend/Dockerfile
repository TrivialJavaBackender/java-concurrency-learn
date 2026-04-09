FROM nginx:1.25-alpine

# Убираем дефолтный конфиг
RUN rm /etc/nginx/conf.d/default.conf

# Конфиг с envsubst-переменными
COPY nginx.conf /etc/nginx/templates/default.conf.template

# Статика
COPY index.html /usr/share/nginx/html/

# nginx:alpine запускает envsubst автоматически для /etc/nginx/templates/*.template
# Нужные переменные: BACKEND_HOST, BACKEND_PORT

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q -O- http://localhost/api/info || exit 1
