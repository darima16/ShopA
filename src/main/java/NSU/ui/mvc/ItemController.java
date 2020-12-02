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

import javax.imageio.ImageIO;
import javax.validation.Valid;

import NSU.ui.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import NSU.ui.ShopRepository;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
	public ModelAndView nothing(){
		return new ModelAndView("layout");
	}

	@RequestMapping(params = "shop")
	public ModelAndView list() {
		Iterable<Item> items = this.shopRepository.findAll();
		return new ModelAndView("items/list", "items", items);
	}

	@RequestMapping("{id}")
	public ModelAndView view(@PathVariable("id") Item item) {
		return new ModelAndView("items/view", "item", item);
	}

//	@RequestMapping("create")
//	public ModelAndView form() {
//		return new ModelAndView("items/form");
//	}

	@RequestMapping(params = "create", method = RequestMethod.GET)
	public String createForm(@ModelAttribute Item item) {
		return "items/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@Valid Item item, BindingResult result,
                               RedirectAttributes redirect) throws IOException {
		if (result.hasErrors()) {
			return new ModelAndView("items/form", "formErrors", result.getAllErrors());
		}
		item = this.shopRepository.save(item);
		System.out.println("AAaa");
		System.out.println(item.getName());
		System.out.println(item.getImage());
		System.out.println(item.getPrice());
		System.out.println("AAaa");
		redirect.addFlashAttribute("globalMessage", "Товар успешно создан");
		return new ModelAndView("redirect:/{item.id}", "item.id", item.getId());
	}

	private final String UPLOAD_DIR = "C:\\Users\\User\\IdeaProjects\\ShopA\\src\\main\\resources\\static\\";

	@RequestMapping(value ="/upload", method = RequestMethod.GET)
	public String homepage() {
		return "items/form";
	}

	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
		System.out.println("AAAAAAAAA");
		System.out.println(file.getOriginalFilename());
		// check if file is empty
		if (file.isEmpty()) {
			attributes.addFlashAttribute("message", "Please select a file to upload.");
			return "redirect:/?create";
		}
		// normalize the file path
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		System.out.println(fileName);
		// save the file on the local file system
		try {
			Path path = Paths.get(UPLOAD_DIR + fileName);

			File convFile = new File(file.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();

			BufferedImage image = ImageIO.read(convFile);
			File output = new File(String.valueOf(path));
			ImageIO.write(image, "png", output);
			//Path path = Paths.get(UPLOAD_DIR + fileName);
			//Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return success response
		attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');

		return "redirect:/create";
	}
//	@RequestMapping(value = "/upload", method = RequestMethod.GET)
//	public String fileUploaded() {
//		return "items/index";
//	}
//
//
//	@RequestMapping(value="/upload", method=RequestMethod.POST)
//	public @ResponseBody String handleFileUpload(@RequestParam("name") String name,
//												 @RequestParam("file") MultipartFile file){
//		if (!file.isEmpty()) {
//			try {
//				byte[] bytes = file.getBytes();
//				BufferedOutputStream stream =
//						new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
//				stream.write(bytes);
//				stream.close();
//				return "Вы удачно загрузили " + name + " в " + name + "-uploaded !";
//			} catch (Exception e) {
//				return "Вам не удалось загрузить " + name + " => " + e.getMessage();
//			}
//		} else {
//			return "Вам не удалось загрузить " + name + " потому что файл пустой.";
//		}
//	}

	@RequestMapping("foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}

}
