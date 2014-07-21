package com.epam.kozhanbergenov.shop.tag;

import com.epam.kozhanbergenov.shop.dao.CategoryDAO;
import com.epam.kozhanbergenov.shop.dao.exception.DAOException;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2CategoryDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Category;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetCategoryList extends TagSupport {
    private static final Logger log = Logger.getLogger(GetCategoryList.class);

    @Override
    public int doStartTag() throws JspException {
        CategoryDAO categoryDAO = new H2CategoryDAO(ConnectionPool.getConnection());
        List<Category> categoryList = null;
        try {
            categoryList = categoryDAO.getAllByParentId(0);
        } catch (DAOException e) {
            log.error(e);
        }
        Locale locale = (Locale) pageContext.findAttribute("language");
        String s;
        if (locale.getLanguage().equals("en"))
            s = "<h3>Categories</h3>";
        else s = "<h3>Категории</h3>";
        for (Category category : categoryList) {
            List<Category> childrenList = null;
            try {
                childrenList = categoryDAO.getAllByParentId(category.getId());
            } catch (DAOException e) {
                log.error(e);
            }
            s += "<strong><a href=\"controller?action=showCategoryItems&parentId=0&categoryId=" + category.getId() + "\">"
                    + (locale.getLanguage().equals("en") ? category.getName() : category.getRuName()) + "</a></strong>";
            if (childrenList != null) {
                s += "<ul>";
                for (Category category1 : childrenList) {
                    s += "<li><a href=\"controller?action=showCategoryItems&parentId=" + category1.getParentId() + "&categoryId=" + category1.getId() + "\">"
                            + (locale.getLanguage().equals("en") ? category1.getName() : category1.getRuName()) + "</a>" + "</li>";
                }
                s += "</ul>";
            }
        }
        categoryDAO.returnConnection();
        try {
            JspWriter out = pageContext.getOut();
            out.write(s);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
