/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package NSU.ui.mvc;

import javax.validation.Valid;

import NSU.ui.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import NSU.ui.ShopRepository;

/**
 * @author Rob Winch
 */
@Controller
@RequestMapping("/")
public class ItemController {
	private final ShopRepository shopRepository;

	@Autowired
	public ItemController(ShopRepository shopRepository) {
		this.shopRepository = shopRepository;
	}

	@RequestMapping
	public ModelAndView list() {
		Iterable<Item> items = this.shopRepository.findAll();
		return new ModelAndView("items/list", "items", items);
	}

	@RequestMapping("{id}")
	public ModelAndView view(@PathVariable("id") Item item) {
		return new ModelAndView("items/view", "item", item);
	}

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(@ModelAttribute Item item) {
		return "items/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@Valid Item item, BindingResult result,
                               RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("items/form", "formErrors", result.getAllErrors());
		}
		item = this.shopRepository.save(item);
		redirect.addFlashAttribute("globalMessage", "Successfully created a new item");
		return new ModelAndView("redirect:/{item.id}", "item.id", item.getId());
	}

	@RequestMapping("foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}

}
