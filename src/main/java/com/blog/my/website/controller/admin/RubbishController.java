package com.blog.my.website.controller.admin;

import com.blog.my.website.controller.BaseController;
import com.blog.my.website.dto.LogActions;
import com.blog.my.website.dto.Types;
import com.blog.my.website.exception.TipException;
import com.blog.my.website.modal.Bo.RestResponseBo;
import com.blog.my.website.modal.Vo.ContentVo;
import com.blog.my.website.modal.Vo.ContentVoExample;
import com.blog.my.website.service.IContentService;
import com.blog.my.website.service.ILogService;
import com.blog.my.website.service.IOptionService;
import com.blog.my.website.service.ISiteService;
import com.blog.my.website.utils.IPKit;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by wangq on 2017/3/20.
 */
@Controller
@RequestMapping("/admin/rubbish")
public class RubbishController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RubbishController.class);

    @Resource
    private IOptionService optionService;

    @Resource
    private ILogService logService;

    @Resource
    private ISiteService siteService;

    @Resource
    private IContentService contentsService;

    /**
     * 系统设置
     */
    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        ContentVoExample contentVoExample = new ContentVoExample();
        contentVoExample.setOrderByClause("created desc");
        contentVoExample.createCriteria().andTypeEqualTo(Types.ARTICLE.getType()).andIsDeleteEqualTo(1);
        PageInfo<ContentVo> contentsPaginator = contentsService.getArticlesWithpage(contentVoExample,page,limit);
        request.setAttribute("articles", contentsPaginator);
        return "admin/rubbish_list";
    }

    /**
     * 撤销删除文章
     * @param cid
     * @param request
     * @return
     */
    @RequestMapping(value = "/revoke")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo revoke(@RequestParam int cid, HttpServletRequest request) {
        try {
            ContentVo contents = contentsService.getContents(cid + "");
            contents.setIsDelete(0);
            contents.setStatus(Types.PUBLISH.getType());
            contentsService.updateContentByCid(contents);
            logService.insertLog(LogActions.REVOKE_ARTICLE.getAction(), cid+"", IPKit.getIpAddrByRequest(request), this.getUid(request));
        } catch (Exception e) {
            String msg = "文章撤销删除失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

    /**
     * 彻底删除文章
     * @param cid
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam int cid, HttpServletRequest request) {
        try {
            contentsService.deleteByCid(cid);
            logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid+"", IPKit.getIpAddrByRequest(request), this.getUid(request));
        } catch (Exception e) {
            String msg = "文章彻底删除失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }
}
