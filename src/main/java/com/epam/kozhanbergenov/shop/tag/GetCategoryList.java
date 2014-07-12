package com.epam.kozhanbergenov.shop.tag;

import com.epam.kozhanbergenov.shop.dao.CategoryDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2CategoryDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Category;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GetCategoryList extends TagSupport {
    private static final Logger log = Logger.getLogger(GetCategoryList.class);

    @Override
    public int doStartTag() throws JspException {
        CategoryDao categoryDao = new H2CategoryDao(ConnectionPool.getConnection());
        List<Category> categoryList = null;
        try {
            categoryList = categoryDao.getAllByParentId(0);
        } catch (SQLException e) {
            log.error(e);
        }

        String s = "<h3>Categories</h3>";
        for (Category category : categoryList) {
            List<Category> childrenList = null;
            try {
                childrenList = categoryDao.getAllByParentId(category.getId());
            } catch (SQLException e) {
                log.error(e);
            }
            s += "<strong><a href=\"controller?action=showCategoryItems&parentId=0&categoryId=" + category.getId() + "\">"
                    + category.getName() + "</a></strong>";
            if (childrenList != null) {
                s += "<ul>";
                for (Category category1 : childrenList) {
                    s += "<li><a href=\"controller?action=showCategoryItems&parentId=" + category1.getParentId() + "&categoryId=" + category1.getId() + "\">"
                            + category1.getName() + "</a>" + "</li>";
                }
                s += "</ul>";
            }

        }

        categoryDao.returnConnection();
        try {
            JspWriter out = pageContext.getOut();
            out.write(s);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
