const authControll = require('../../controller/auth_controller/auth_controller');
const tokenControll = require('../../controller/auth_controller/auth_token_controller')
const express = require('express');

const route = express.Router();

route.post('/check-token', tokenControll.check_token)
route.post('/login', authControll.login)
route.post('/forget-password', authControll.forget_password)
route.post('/verify-code', authControll.verify_code)
route.post('/reset-password', authControll.reset_password)
route.post('/sign-up', authControll.sign_up)
route.post('/create-profile', authControll.creat_profile)

module.exports = route;