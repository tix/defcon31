<?
exec('mysqldump --user=panel3000 --password=Galaperedol@1051982 --host=localhost panel3000 > /var/www/www-root/data/www/panel.top/bkp/' . time() . '.sql');
?>