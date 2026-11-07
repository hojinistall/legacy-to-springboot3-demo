package com.company.board.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.company.board.dao.BoardDAO;

@Controller
public class BoardController {

    @Autowired
    private BoardDAO boardDAO;

    @RequestMapping("/board/list.do")
    public String list(HttpServletRequest request, Model model) throws Exception {
        String keyword = request.getParameter("keyword");
        // [문제5] page에 숫자가 아닌 값이 오면 NumberFormatException → 500 에러 화면 노출
        String pageParam = request.getParameter("page");
        int page = pageParam == null ? 1 : Integer.parseInt(pageParam);

        List<Map<String, Object>> list = boardDAO.getList(keyword, page);
        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);
        return "board/list";
    }

    // [문제6] GET으로도 글이 등록됨. 입력값 검증이 전혀 없음 (빈 제목, 1만 자 제목 모두 통과)
    @RequestMapping("/board/write.do")
    public String write(HttpServletRequest request) throws Exception {
        boardDAO.insert(
                request.getParameter("title"),
                request.getParameter("writer"),
                request.getParameter("content"));
        return "redirect:/board/list.do";
    }
}
