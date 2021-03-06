package com.meal.seller.controller;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.meal.admin.service.AdminService;
import com.meal.admin.vo.AdminVO;
import com.meal.board.gq.service.BoardGqService;
import com.meal.board.gq.vo.BoardGqVO;
import com.meal.board.gr.service.BoardGrService;
import com.meal.board.gr.vo.BoardGrVO;
import com.meal.board.one.service.Board1Service;
import com.meal.board.one.vo.Board1VO;
import com.meal.common.controller.BaseController;
import com.meal.goods.service.GoodsService;
import com.meal.goods.vo.GoodsVO;
import com.meal.order.service.OrderService;
import com.meal.order.vo.OrderVO;
import com.meal.seller.service.SellerService;
import com.meal.seller.vo.Img_sVO;
import com.meal.seller.vo.SellerVO;

@Controller("sellerController")
@RequestMapping("/seller")
public class SellerControllerImpl extends BaseController implements SellerController {

	private String CURR_IMAGE_UPLOAD_PATH = "C:\\Meal\\Image";
	@Autowired
	private SellerService sellerService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private SellerVO sellerVO;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private BoardGqService boardGqService;
	@Autowired
	private Board1Service board1Service;
	@Autowired
	private BoardGrService boardGrService;

	@Autowired
	private Img_sVO img_sVO;
	@Autowired
	BCryptPasswordEncoder passwordEncode;

	@Override
	@RequestMapping(value = "/logout.do", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		session.setAttribute("isLogOn", false);
		session.removeAttribute("sellerInfo");
		session.removeAttribute("quickZzimList");
		session.removeAttribute("quickZzimListNum");
		String message = "??????????????? ?????????????????????.";
		mav.addObject("message", message);
		mav.setViewName("redirect:/main/main.do");
		return mav;
	}

	@Override
	@RequestMapping(value = "/addSeller.do", method = RequestMethod.POST)
	public ModelAndView addSeller(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		String imageFileName = null;

		HashMap<String, Object> newSellerMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			System.out.println("name + value = " + name + " : " + value);
			newSellerMap.put(name, value);
			// ????????? ??????????????? ????????? ?????? ???????????? ?????? ?????????INFO??? ?????????
		}
		// ???????????? ????????? ??? ????????? ?????? ?????? ???????????????
		String s_pw = (String) newSellerMap.get("s_pw");
		String encodeu_pw = passwordEncode.encode(s_pw);
		// ???????????? ????????? ??????
		newSellerMap.put("s_pw", encodeu_pw);
		// ?????? info>DB
		sellerService.addSeller(newSellerMap);
		// ??????????????? ??????????????? ?????? ???????????? ??????????????? ????????? ???????????? ?????? ??????

		String s_id = (String) newSellerMap.get("s_id");

		// ????????? ??????????????? ???????????? ????????? ????????? C\Meal\Image\temp??? ???????????????
		List<HashMap<String, Object>> imageFileList = (List<HashMap<String, Object>>) upload(multipartRequest);

		// ????????? ????????? ?????? ?????????
		try {
			if (imageFileList != null && imageFileList.size() != 0) {
				for (HashMap<String, Object> item : imageFileList) {
					// ????????? ???????????? ????????? ???????????? MAP??? ????????? ???????????? ????????????
					item.put("s_id", s_id);
					// ????????? ????????????????????? ???????????? ??????
					imageFileName = (String) item.get("fileName");
					String cate = (String) item.get("cate");
					if (!(imageFileName.equals("") || imageFileName == null)) {
						// ???????????? ???????????? ????????? DB??? ?????? s_id | cate = fileName |fileName = originalfileName
						sellerService.addSellerImg(item);
						// temp??? ?????? ????????????????????? ??????
						File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + imageFileName);
						// ??????????????? ?????? ????????? ???????????? ??????
						File destDir = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "seller" + "\\" + s_id + "\\" + cate);
						// ??????
						FileUtils.moveFileToDirectory(srcFile, destDir, true);
					}
				}
			}
			// ???????????? ??????????????? ?????? ????????? ????????? ????????????
			SellerVO sellerInfo = (SellerVO) sellerService.decode(s_id);
			sellerInfo.setS_pw(s_pw);
			mav.addObject("SellerInfo", sellerInfo);
			String viewName = "/member/memberResult";
			mav.setViewName(viewName);
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			// ????????? ????????? temp??? ?????? ???????????? ????????? ???????????? ?????? ??????
			if (imageFileList != null && imageFileList.size() != 0) {
				for (HashMap<String, Object> item : imageFileList) {
					imageFileName = (String) item.get("fileName");
					File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + imageFileName);
					srcFile.delete();
				}
			}

			String viewName1 = "/seller/sellerForm";
			mav.setViewName(viewName1);
			return mav;
		}
	}

	@Override
	@RequestMapping(value = "/updateseller.do", method = RequestMethod.POST)
	public ResponseEntity updateSeller(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {

		HttpSession session = multipartRequest.getSession();
		HashMap<String, Object> newSellerMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			// ??????????????? ?????? ????????? map??? ???????????? ????????????
			// ?????????????????? ???????????????
			if (value == null) {
				continue;
			}
			System.out.println("name + value = " + name + " : " + value);
			newSellerMap.put(name, value);
			// ????????? ??????????????? ????????? ?????? ???????????? ?????? ?????????INFO??? ?????????
		}

		String message = null;
		ResponseEntity resEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			// ??????????????? ????????? ???????????? id = ??????????????? ?????? pw = ???????????? ?????? ??????
			String s_id = (String) newSellerMap.get("s_id");
			String s_pw = (String) newSellerMap.get("s_pw");
			String encodeS_pw = passwordEncode.encode(s_pw);
			newSellerMap.put("s_pw", encodeS_pw);
			// ?????? ?????????????????? ????????????????????? ?????????
			List<Img_sVO> OldImgList = (List<Img_sVO>) sellerService.selectSellerImg(s_id);
			// ???????????? ???????????? ????????? ?????????
			List<HashMap<String, Object>> imageFileList = (List<HashMap<String, Object>>) upload(multipartRequest);
			// ?????? ???????????? ??????????????? ??????
			for (Img_sVO oldI : OldImgList) {
				for (HashMap<String, Object> newI : imageFileList) {
					String oldCate = oldI.getCate();
					String newCate = (String) newI.get("cate");
					String newImgFileName = (String) newI.get("fileName");
					String oldImgFileName = oldI.getFileName();
					newI.put("s_id", s_id);
					// ?????? ?????????????????? ????????? ????????? ????????? ?????? ?????????
					if (oldCate.equals(newCate)) {

						// ????????? ???????????? ?????? ?????? ?????????????????? ???????????????
						if (oldImgFileName != null && newImgFileName != null) {
							String oldpath = CURR_IMAGE_UPLOAD_PATH + "\\" + "seller" + "\\" + s_id + "\\" + oldCate;
							deleteFolder(oldpath);
							// ????????? ????????? ????????????(upload)???????????? path????????? ?????? ?????????
							File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + newImgFileName);
							// ??????????????? ?????? ????????? ???????????? ??????
							File destDir = new File(
									CURR_IMAGE_UPLOAD_PATH + "\\" + "seller" + "\\" + s_id + "\\" + newCate);
							// ??????
							FileUtils.moveFileToDirectory(srcFile, destDir, true);
							// db??? ?????? ????????? ?????? ???????????????
							sellerService.updateSellerImg(newI);

							// ?????? ????????? ????????? ?????????
						} else if (oldImgFileName == null && newImgFileName != null) {
							// ????????? ????????? ????????????(upload)???????????? path????????? ?????? ?????????
							File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + newImgFileName);
							// ??????????????? ?????? ????????? ???????????? ??????
							File destDir = new File(
									CURR_IMAGE_UPLOAD_PATH + "\\" + "seller" + "\\" + s_id + "\\" + newCate);
							// ??????
							FileUtils.moveFileToDirectory(srcFile, destDir, true);
							// DB??? ??????
							sellerService.addSellerImg(newI);

						}

					}
				}
			}

			// db?????? ?????? ?????? ??????
			sellerService.updateSeller(newSellerMap);

			message = "<script>";
			message += " alert('??????????????? ?????????????????????..');";
			message += " location.href='" + multipartRequest.getContextPath() + "/member/memberResult.do';";
			message += " </script>";
			SellerVO sellerInfo = (SellerVO) sellerService.decode(s_id);
			sellerInfo.setS_id(s_id);
			sellerInfo.setS_pw(s_pw);
			session.setAttribute("sellerInfo", sellerInfo);
		} catch (Exception e) {

			message = "<script>";
			message += " alert('?????? ????????? ??????????????????');";
			message += " location.href='" + multipartRequest.getContextPath() + "/seller/sellerForm.do';";
			message += " </script>";
			e.printStackTrace();
		}
		resEntity = new ResponseEntity(message, responseHeaders, HttpStatus.OK);
		return resEntity;
	}

	@Override
	@RequestMapping(value = "/deleteSeller.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView deleteSeller(@RequestParam HashMap<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		SellerVO sellerInfo = (SellerVO) session.getAttribute("sellerInfo");
		SellerVO sellerInfo1 = (SellerVO) sellerService.decode(sellerInfo.getS_id());
		String pw = (String) map.get("pw");
		// ?????? ???????????? ??????
		if (passwordEncode.matches(pw, sellerInfo1.getS_pw())) {
			// ?????? ????????? ?????? ????????? ?????? ??? ????????? ?????? ?????? ???????????? (???????????? ?????? ????????? ???????????? ?????? ????????? ??????)
			String path = CURR_IMAGE_UPLOAD_PATH + "\\" + "seller" + "\\" + sellerInfo.getS_id();
			// base controller ????????? ?????? ?????? ????????? ??????
			deleteFolder(path);
			// seller??? ?????? ?????? ??????
			sellerService.deleteSeller(sellerInfo);
			// ???????????? db??????

			adminService.insertReason(map);
			session.setAttribute("isLogOn", false);
			session.removeAttribute("memberInfo");
			session.removeAttribute("sellerInfo");
			session.removeAttribute("adminInfo");
			String message = "????????? ?????? ???????????????.";
			mav.addObject("message", message);
			String viewName = "redirect:/main/main.do";
			mav.setViewName(viewName);
			return mav;
		}
		mav.setViewName("/main/loginForm");
		return mav;
	}

	// ????????? ????????? ????????? ?????? ??? ?????????????????? ???????????????
	@Override
	@RequestMapping(value = "/sellerDetail.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView sellerDetail(@RequestParam("id") String id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		String viewName = (String) request.getAttribute("viewName");
		mav.setViewName(viewName);
		SellerVO sellerInfo = (SellerVO) sellerService.decode(id);

		if (sellerVO != null) {
			mav.addObject("sellerVO", sellerInfo);

			// orderList (s_id) ???????????? + ??? ????????? + ?????? ????????? ?????? ????????? ?????????????????? ??? .
			// goodsList(s_id) ????????????????????? ??????????????? ????????????

		} else {
			String message = "????????? ?????????????????????.";
			mav.addObject("message", message);
			mav.setViewName("redirect:/main/main.do");
			return mav;
		}

		// ????????? ????????? ?????? ( ????????? get???????????? ??????????????????)
		HttpSession session = request.getSession();
		SellerVO sellerLog = (SellerVO) session.getAttribute("sellerLog");
		AdminVO adminLog = (AdminVO) session.getAttribute("adminInfo");
		if (sellerLog != null || adminLog != null) {
			return mav;
		} else {
			String message = "????????? ????????? ?????????????????????.";
			mav.addObject("message", message);
			mav.setViewName("redirect:/main/main.do");
			return mav;
		}
	}

	// ????????? ??????????????? ?????? ???????????? ??? ??????????????? ?????????????????? ?????? 0614
	@Override
	@RequestMapping(value = "/sellerMypage.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView sellerMypage(@RequestParam(value = "s_id", required = false) String s_id,
			@RequestParam(value = "dateMap", required = false) Map<String, Object> dateMap,
			@RequestParam(value = "section", required = false) String section,
			@RequestParam(value = "pageNum", required = false) String pageNum, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		SellerVO sellerVO = (SellerVO) session.getAttribute("sellerInfo");
		AdminVO adminVO = (AdminVO) session.getAttribute("adminInfo");
		// ?????? admin??? ????????? ??????(s_id)
		if (sellerVO != null) {
			String s_id1 = sellerVO.getS_id();
			String viewName = (String) request.getAttribute("viewName");
			mav.setViewName(viewName);

			HashMap<String, Object> pagingInfo = new HashMap<String, Object>();
			pagingInfo.put("section", section);
			pagingInfo.put("pageNum", pageNum);

			HashMap<String, Object> pagingMap = (HashMap<String, Object>) paging(pagingInfo);

			pagingMap.put("s_id", s_id1);
			// ??????????????? ????????? ?????? ??????
			List<GoodsVO> goodsList = goodsService.selectGoodsPage(pagingMap);

			// ???????????? ??????
			List<OrderVO> orderList = orderService.orderSellerList(pagingMap);
			// ????????? ?????? ??????
			List<OrderVO> orderCanceledSellerList = orderService.orderCanceledSellerList(pagingMap);

			System.out.println("---------------------------");
			System.out.println("goodsList : " + goodsList);
			System.out.println("---------------------------");
			System.out.println("sellerVO : " + sellerVO);
			System.out.println("---------------------------");
			System.out.println("orderList : " + orderList);
			System.out.println("---------------------------");

			mav.addObject("sellerVO", sellerVO);
			mav.addObject("orderList", orderList);
			mav.addObject("goodsList", goodsList);
			mav.setViewName(viewName);
			mav.addObject("orderCanceledSellerList", orderCanceledSellerList);
			return mav;
		}else if (adminVO != null) {
			sellerVO = (SellerVO) sellerService.decode(s_id);
			String viewName = (String) request.getAttribute("viewName");
			mav.setViewName(viewName);

			HashMap<String, Object> pagingInfo = new HashMap<String, Object>();
			pagingInfo.put("section", section);
			pagingInfo.put("pageNum", pageNum);

			HashMap<String, Object> pagingMap = (HashMap<String, Object>) paging(pagingInfo);

			pagingMap.put("s_id", s_id);
			// ??????????????? ????????? ?????? ??????
			List<GoodsVO> goodsList = goodsService.selectGoodsPage(pagingMap);

			// ???????????? ??????
			List<OrderVO> orderList = orderService.orderSellerList(pagingMap);
			// ????????? ?????? ??????
			List<OrderVO> orderCanceledSellerList = orderService.orderCanceledSellerList(pagingMap);

			System.out.println("---------------------------");
			System.out.println("goodsList : " + goodsList);
			System.out.println("---------------------------");
			System.out.println("sellerVO : " + sellerVO);
			System.out.println("---------------------------");
			System.out.println("orderList : " + orderList);
			System.out.println("---------------------------");


			mav.addObject("sellerVO", sellerVO);
			mav.addObject("orderList", orderList);
			mav.addObject("goodsList", goodsList);
			mav.addObject("orderCanceledSellerList", orderCanceledSellerList);
			mav.setViewName(viewName);

			return mav;
		}
		String message = "????????? ?????????????????????.";
		mav.addObject("message", message);
		String viewName1 = "redirect:/main/main.do";
		mav.setViewName(viewName1);
		return mav;
		
	
	}

	
	
	@Override
	@RequestMapping(value = "/sellerBoardMypage.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView sellerBoardMypage(@RequestParam(value = "s_id", required = false) String s_id,
			@RequestParam(value = "dateMap", required = false) Map<String, Object> dateMap,
			@RequestParam(value = "section", required = false) String section,
			@RequestParam(value = "pageNum", required = false) String pageNum,
			@RequestParam(value = "message", required = false) String message,HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();

		System.out.println("??????");

		SellerVO sellerVO = (SellerVO) session.getAttribute("sellerInfo");
		AdminVO adminVO = (AdminVO) session.getAttribute("adminInfo");
		if (sellerVO != null) {
			String s_id1 = sellerVO.getS_id();
			String viewName = (String) request.getAttribute("viewName");
			mav.setViewName(viewName);

			HashMap<String, Object> pagingInfo = new HashMap<String, Object>();
			pagingInfo.put("section", section);
			pagingInfo.put("pageNum", pageNum);

			HashMap<String, Object> pagingMap = (HashMap<String, Object>) paging(pagingInfo);

			pagingMap.put("s_id", s_id1);
			// ??????????????? ????????? ?????? ??????

			// 1???1?????? ?????? ???????????? ?????? 0615
			List<Board1VO> board1SellerList = board1Service.selectMyBoard1List(pagingMap);
			// Board1 ???????????? ????????? ?????? ?????? 0615
			List<Board1VO> board1A = board1Service.selectBoard1allList();
			;
			// ???????????? ?????? ?????? 0615
			List<BoardGqVO> boardGqSellerList = boardGqService.boardGqSellerList(pagingMap);
			// ???????????? ???????????? ????????? ?????? ?????? 0615
			List<BoardGqVO> boardGqA = boardGqService.selectBoardGqallList();
			// ???????????? ???????????? 0616
			List<BoardGrVO> boardGrSellerList = boardGrService.selectBoardGrSList(pagingMap);
			// ???????????? ???????????? ????????? ?????? ?????? 0616
			List<BoardGrVO> boardGrA = boardGrService.selectBoardGrallList();
		

			for (Board1VO item : board1SellerList) {
				for (Board1VO a : board1A) {
					if (!((int) item.getB_1_id() == (int) a.getParentNo())) {
						String compare = "N";
						item.setCompare(compare);
					} else {
						String compare = "Y";
						item.setCompare(compare);
						System.out.println("---------------------------");
						System.out.println("Board1Compare" + item.getB_1_id());
						System.out.println("---------------------------");
						break;
					}
				}
			}

			for (BoardGqVO item : boardGqSellerList) {
				for (BoardGqVO a : boardGqA) {
					if (!((int) item.getB_gq_id() == (int) a.getParentNo())) {
						String compare = "N";
						item.setCompare(compare);
					} else {
						String compare = "Y";
						item.setCompare(compare);
						System.out.println("---------------------------");
						System.out.println("BoardGqCompare" + item.getB_gq_id());
						System.out.println("---------------------------");
						break;
					}
				}
			}

			for (BoardGrVO item : boardGrSellerList) {
				for (BoardGrVO a : boardGrA) {
					if (!((int) item.getB_gr_id() == (int) a.getParentNo())) {
						String compare = "N";
						item.setCompare(compare);
					} else {
						String compare = "Y";
						item.setCompare(compare);
						System.out.println("---------------------------");
						System.out.println("BoardGrCompare" + item.getB_gr_id());
						System.out.println("---------------------------");
						break;
					}
				}
			}

			mav.addObject("boardGqSellerList", boardGqSellerList);
			mav.addObject("board1SellerList", board1SellerList);
			mav.addObject("boardGrSellerList", boardGrSellerList);
			mav.addObject("sellerVO", sellerVO);
			mav.setViewName(viewName);
			if (message != null) {
				mav.addObject("message", message);
			}

			return mav;
			
		} else if (adminVO != null) {

			
			System.out.println("??????");
			String viewName = (String) request.getAttribute("viewName");
			mav.setViewName(viewName);

			HashMap<String, Object> pagingInfo = new HashMap<String, Object>();
			pagingInfo.put("section", section);
			pagingInfo.put("pageNum", pageNum);

			HashMap<String, Object> pagingMap = (HashMap<String, Object>) paging(pagingInfo);

			// s_id > SellerVO ?????????
			sellerVO = (SellerVO) sellerService.decode(s_id);

			pagingMap.put("s_id", s_id);
			// ??????????????? ????????? ?????? ??????

			// 1???1?????? ?????? ???????????? ?????? 0615
			List<Board1VO> board1SellerList = board1Service.selectMyBoard1List(pagingMap);
			// Board1 ???????????? ????????? ?????? ?????? 0615
			List<Board1VO> board1A = board1Service.selectBoard1allList();
			;
			// ???????????? ?????? ?????? 0615
			List<BoardGqVO> boardGqSellerList = boardGqService.boardGqSellerList(pagingMap);
			// ???????????? ???????????? ????????? ?????? ?????? 0615
			List<BoardGqVO> boardGqA = boardGqService.selectBoardGqallList();
			// ???????????? ???????????? 0616
			List<BoardGrVO> boardGrSellerList = boardGrService.selectBoardGrSList(pagingMap);
			// ???????????? ???????????? ????????? ?????? ?????? 0616
			List<BoardGrVO> boardGrA = boardGrService.selectBoardGrallList();

			for (Board1VO item : board1SellerList) {
				for (Board1VO a : board1A) {
					if (!((int) item.getB_1_id() == (int) a.getParentNo())) {
						String compare = "N";
						item.setCompare(compare);
					} else {
						String compare = "Y";
						item.setCompare(compare);
						System.out.println("---------------------------");
						System.out.println("Board1Compare" + item.getB_1_id());
						System.out.println("---------------------------");
						break;
					}
				}
			}

			for (BoardGqVO item : boardGqSellerList) {
				for (BoardGqVO a : boardGqA) {
					if (!((int) item.getB_gq_id() == (int) a.getParentNo())) {
						String compare = "N";
						item.setCompare(compare);
					} else {
						String compare = "Y";
						item.setCompare(compare);
						System.out.println("---------------------------");
						System.out.println("BoardGqCompare" + item.getB_gq_id());
						System.out.println("---------------------------");
						break;
					}
				}
			}

			for (BoardGrVO item : boardGrSellerList) {
				for (BoardGrVO a : boardGrA) {
					if (!((int) item.getB_gr_id() == (int) a.getParentNo())) {
						String compare = "N";
						item.setCompare(compare);
					} else {
						String compare = "Y";
						item.setCompare(compare);
						System.out.println("---------------------------");
						System.out.println("BoardGrCompare" + item.getB_gr_id());
						System.out.println("---------------------------");
						break;
					}
				}
			}
			System.out.println("---------------------------");
			System.out.println("sellerVO : " + sellerVO);
			System.out.println("---------------------------");
			System.out.println("boardGqSellerList : " + boardGqSellerList);
			System.out.println("---------------------------");
			System.out.println("board1SellerList : " + board1SellerList);
			System.out.println("---------------------------");
			System.out.println("boardGrSellerList : " + boardGrSellerList);
			System.out.println("---------------------------");

			mav.addObject("boardGqSellerList", boardGqSellerList);
			mav.addObject("board1SellerList", board1SellerList);
			mav.addObject("boardGrSellerList", boardGrSellerList);
			mav.addObject("sellerVO", sellerVO);
			mav.setViewName(viewName);
			if (message != null) {
				mav.addObject("message", message);
			}

			return mav;

		} else {
			message = "????????? ?????????????????????.";
			mav.addObject("message", message);
			String viewName1 = "redirect:/main/main.do";
			mav.addObject("message", message);
			mav.setViewName(viewName1);
			return mav;
		}
	}

}