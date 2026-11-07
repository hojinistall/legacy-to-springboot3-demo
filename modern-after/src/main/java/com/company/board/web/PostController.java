package com.company.board.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.board.domain.PostForm;
import com.company.board.service.PostService;

@Controller
@RequestMapping("/board")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {
        model.addAttribute("result", postService.findPage(keyword, page));
        model.addAttribute("keyword", keyword);
        return "board/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("form", PostForm.empty());
        return "board/form";
    }

    // [해결6] 등록은 POST만 허용하고 입력값을 검증
    @PostMapping
    public String write(@Validated @ModelAttribute("form") PostForm form, BindingResult binding) {
        if (binding.hasErrors()) {
            return "board/form";
        }
        postService.write(form);
        return "redirect:/board";
    }
}
