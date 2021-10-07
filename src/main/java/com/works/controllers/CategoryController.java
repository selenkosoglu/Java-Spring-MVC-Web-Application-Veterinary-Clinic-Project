package com.works.controllers;

import com.works.entities.Category;
import com.works.entities.Supplier;
import com.works.repositories.CategoryRepository;
import com.works.utils.Util;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @GetMapping("")
    public String category(Model model, Category category) {
        model.addAttribute("category", category);
        model.addAttribute("isValidName", false);
        model.addAttribute("isError", false);
        return "category";
    }

    Category categoryUpdate = new Category();
    Integer processType = null;

    @PostMapping("/insert")
    public String categoryInsert(@Valid @ModelAttribute("category") Category category, BindingResult bindingResult, Model model) {
        System.out.println("*******************PROCESS-TYPE*********************" + processType);
        boolean isValidName = false;
        if (!bindingResult.hasErrors()) {
            try {
                if (processType == 1) {
                    categoryUpdate = new Category();
                } else {
                    category.setCategory_id(categoryUpdate.getCategory_id());
                }
                categoryRepository.saveAndFlush(category);
                categoryUpdate = new Category();
                return "redirect:/category";
            } catch (DataIntegrityViolationException ex) {
                Util.log("CategoryControllerError : " + "Bu kategori ismi sistemde mevcut." + ex, this.getClass());
                isValidName = true;
                model.addAttribute("validNameError", "Bu kategori ismi sistemde mevcut.");
            } catch (Exception ex) {
                Util.log("categoryInsert : " + ex, this.getClass());
            }
        }
        model.addAttribute("isValidName", isValidName);
        model.addAttribute("isError", true);
        return "category";
    }

    @GetMapping("/getAllCategory")
    @ResponseBody
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @GetMapping("/delete/{index}")
    @ResponseBody
    public Boolean deleteCategory(@PathVariable String index) {
        Boolean isValidCategory = false;
        try {
            Integer categoryId = Integer.parseInt(index);
            isValidCategory = categoryRepository.existsById(categoryId);
            if (isValidCategory) {
                categoryRepository.deleteById(categoryId);
                categoryUpdate = new Category();
            }
        } catch (Exception ex) {
            Util.log("deleteCategoryError : " + ex, this.getClass());
        }
        return isValidCategory;
    }

    @GetMapping("/update/{index}")
    @ResponseBody
    public List<Object> updateCategoryUpdate(@PathVariable String index) {
        List<Object> info = new ArrayList<>();
        try {
            Integer categoryId = Integer.parseInt(index);
            Boolean isValidCategory = categoryRepository.existsById(categoryId);
            if (isValidCategory) {
                categoryUpdate = categoryRepository.findById(categoryId).get();
                info.add(true);
                info.add(categoryUpdate);
            } else {
                // aranan category yok, sisteme müdahale edilmiş
                info.add(false);
            }
        } catch (Exception ex) {
            Util.log("updateCategoryError : " + ex, this.getClass());
        }
        return info;
    }

    @GetMapping("/processtype/{strIndex}")
    @ResponseBody
    public boolean processType(@PathVariable String strIndex) {
        System.out.println("strIndex = " + strIndex);
        try {
            Integer index = Integer.parseInt(strIndex);
            if (index != 3) {
                processType = index;
            }
        } catch (Exception ex) {
            Util.log("processTypeError : " + ex, this.getClass());

        }
        return true;
    }

    @GetMapping("/search/{strSearch}")
    @ResponseBody
    public List<Category> getCategorySearch(@PathVariable String strSearch) {
        try {
            return categoryRepository.findByCategory_titleContainsIgnoreCase(strSearch.trim());
        } catch (Exception ex) {
            Util.log("categorySearch Error : " + ex, this.getClass());
        }
        return null;
    }
}
