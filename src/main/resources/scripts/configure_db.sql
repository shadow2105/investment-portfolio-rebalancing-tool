-- Create databases
CREATE DATABASE iprt_dev;
CREATE DATABASE iprt_prod;

-- Create database service accounts
CREATE USER 'iprt_dev_user'@'%' IDENTIFIED BY 'shd_dev66';
CREATE USER 'iprt_prod_user'@'%' IDENTIFIED BY 'shd_prod99';

-- Database grants (only DML)
GRANT SELECT ON iprt_dev.* TO 'iprt_dev_user'@'%';
GRANT INSERT ON iprt_dev.* TO 'iprt_dev_user'@'%';
GRANT UPDATE ON iprt_dev.* TO 'iprt_dev_user'@'%';
GRANT DELETE ON iprt_dev.* TO 'iprt_dev_user'@'%';

GRANT SELECT ON iprt_prod.* TO 'iprt_prod_user'@'%';
GRANT INSERT ON iprt_prod.* TO 'iprt_prod_user'@'%';
GRANT UPDATE ON iprt_prod.* TO 'iprt_prod_user'@'%';
GRANT DELETE ON iprt_prod.* TO 'iprt_prod_user'@'%';
