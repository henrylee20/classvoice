package me.henrylee.classvoice.controller;

import me.henrylee.classvoice.model.Student;
import me.henrylee.classvoice.model.Teacher;
import me.henrylee.classvoice.model.Token;
import me.henrylee.classvoice.model.response.ErrMsg;
import me.henrylee.classvoice.model.response.LoginResponse;
import me.henrylee.classvoice.service.UserService;
import me.henrylee.classvoice.utils.WXLogin;
import me.henrylee.classvoice.utils.WXUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService service;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    private LoginResponse createResponse(ErrMsg errMsg, Token token, WXUserInfo userInfo) {
        LoginResponse response = null;
        switch (errMsg) {
            case OK:
                response = new LoginResponse(errMsg, token.getUserType(), token.getOpenid(), token.getToken());
                break;
            case USER_UNREGISTER:
                response = new LoginResponse(errMsg, "", userInfo.getOpenid(), "");
                break;
            case WX_AUTH_ERR:
            case USER_REGISTER_ERR:
            default:
                response = new LoginResponse(errMsg);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public LoginResponse login(@RequestParam("code") String code) {
        WXUserInfo userInfo;

        // TODO this is just for debug. delete before release
        switch (code) {
            case "studentDebugCode":
                userInfo = new WXUserInfo();
                userInfo.setOpenid("studentDebugOpenid");
                userInfo.setSessionKey("studentDebugSessionKey");
                break;
            case "teacherDebugCode":
                userInfo = new WXUserInfo();
                userInfo.setOpenid("teacherDebugOpenid");
                userInfo.setSessionKey("teacherDebugSessionKey");
                break;
            case "teacherDebugCode2":
                userInfo = new WXUserInfo();
                userInfo.setOpenid("oUfXb4mE-10ev-iEiEiqnhwCidqs");
                userInfo.setSessionKey("teacherDebugSessionKey2");
                break;
            default:

                // TODO this is for release, reserve it when release.
                try {
                    userInfo = WXLogin.login(code);
                } catch (Exception e) {
                    logger.error("getting openid from wx raised error. msg: {}", e.getMessage());
                    return createResponse(ErrMsg.WX_AUTH_ERR, null, null);
                }

                break;
        }

        if (userInfo == null) {
            logger.warn("cannot get openid from wx");
            return createResponse(ErrMsg.WX_AUTH_ERR, null, null);
        }

        Token token = service.userLogin(userInfo.getOpenid());
        if (token == null) {
            logger.info(String.format("Login failed, no such user, openid: [%s]", userInfo.getOpenid()));
            return createResponse(ErrMsg.USER_UNREGISTER, null, userInfo);
        }

        return createResponse(ErrMsg.OK, token, userInfo);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/registerStudent")
    public LoginResponse registerStudent(@RequestParam("openid") String openid, @RequestBody Student data) {
        logger.debug(data.getName() + ", " + data.getNo() + ", " + data.getClassName());

        data.setOpenid(openid);
        if (service.studentAdd(data)) {
            Token token = service.userLogin(openid);
            return createResponse(ErrMsg.OK, token, null);
        } else {
            logger.warn("student register failed");
            return createResponse(ErrMsg.USER_REGISTER_ERR, null, null);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/registerTeacher")
    public LoginResponse registerTeacher(@RequestParam("openid") String openid, @RequestBody Teacher data) {
        data.setOpenid(openid);
        if (service.teacherAdd(data)) {
            Token token = service.userLogin(openid);
            return createResponse(ErrMsg.OK, token, null);
        } else {
            logger.warn("teacher register failed");
            return createResponse(ErrMsg.USER_REGISTER_ERR, null, null);
        }
    }
}
