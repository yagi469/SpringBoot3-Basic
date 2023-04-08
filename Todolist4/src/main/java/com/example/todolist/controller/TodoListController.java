package com.example.todolist.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.service.TodoService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class TodoListController {
	private final TodoRepository todoRepository;
	private final TodoService todoService;
	private final HttpSession session;
	
	@GetMapping("/todo")
	public ModelAndView showTodoList(ModelAndView mv) {
		// 一覧を検索して表示する
		mv.setViewName("todoList");
		List<Todo> todoList = todoRepository.findAll();
		mv.addObject("todoList", todoList);
		mv.addObject("todoQuery", new TodoQuery()); // Todolist4で追加
		return mv;
	}
	
	@PostMapping("/todo/create/form")
	public ModelAndView createTodo(ModelAndView mv) {
		mv.setViewName("todoForm");
		mv.addObject("todoData", new TodoData());
		session.setAttribute("mode", "create");
		return mv;
	}
	
	//ToDo追加処理(TodoList2で追加したものをTodolist3で改善)
	@PostMapping("/todo/create/do")
	public String createTodo(@ModelAttribute @Validated TodoData todoData, 
			BindingResult result, ModelAndView mv) {
		// エラーチェック
		boolean isValid = todoService.isValid(todoData, result);
		if (!result.hasErrors() && isValid) {
			// エラーなし
			Todo todo = todoData.toEntity();
			todoRepository.saveAndFlush(todo);
			return "redirect:/todo";
		} else {
			// エラーあり
			// mv.setViewName("todoForm");
			// mv.addObject("todoData", todoData);
			return "todoForm";
		}
	}
	
	@PostMapping("/todo/cancel")
	public String cancel() {
		return "redirect:/todo";
	}
	
	@GetMapping("/todo/{id}")
	public ModelAndView todoById(@PathVariable(name="id") int id, ModelAndView mv) {
		mv.setViewName("todoForm");
		Todo todo = todoRepository.findById(id).get();
		mv.addObject("todoData", todo);
		session.setAttribute("mode", "update");
		return mv;
	}
	
	@PostMapping("/todo/update")
	public String updateTodo(@ModelAttribute @Validated TodoData todoData,
			BindingResult result, Model model) {
		// エラーチェック
		boolean isValid = todoService.isValid(todoData, result);
		if (!result.hasErrors() && isValid) {
			// エラーなし
			Todo todo = todoData.toEntity();
			todoRepository.saveAndFlush(todo);
			return "redirect:/todo";
		} else {
			// エラーあり
			// model.addAttribute("todoData", todoData);
			return "todoForm";
		}
	}
	
	@PostMapping("/todo/delete")
	public String deleteTodo(@ModelAttribute TodoData todoData) {
		todoRepository.deleteById(todoData.getId());
		return "redirect:/todo";
	}
	
	@PostMapping("/todo/query")
	public ModelAndView queryTodo(@ModelAttribute TodoQuery todoQuery,
			BindingResult result, ModelAndView mv) {
		mv.setViewName("todoList");
		List<Todo> todoList = null;
		if (todoService.isValid(todoQuery, result)) {
			// エラーが無ければ検索
			todoList = todoService.doQuery(todoQuery);
		}
		// mv.addObject("todoQuery", todoQuery);
		mv.addObject("todoList", todoList);
		return mv;
	}
}
