package com.meal.goods.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.meal.board.gq.service.BoardGqService;
import com.meal.board.gq.vo.BoardGqVO;
import com.meal.board.gr.service.BoardGrService;
import com.meal.board.gr.vo.BoardGrVO;
import com.meal.common.controller.BaseController;
import com.meal.goods.service.GoodsService;
import com.meal.goods.vo.GoodsVO;
import com.meal.goods.vo.Img_gVO;
import com.meal.seller.service.SellerService;
import com.meal.seller.vo.SellerVO;

@Controller("GoodsController")
@RequestMapping("/goods")
public class GoodsControllerImpl extends BaseController implements GoodsController {
	private static final String CURR_IMAGE_UPLOAD_PATH = "C:\\Meal\\Image";
	private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private Img_gVO img_gVO;
	@Autowired
	private SellerVO sellerVO;
	@Autowired
	private GoodsVO goodsVO;
	@Autowired
	private SellerService sellerService;
	@Autowired
	private BoardGrService boardGrService;
	@Autowired
	private BoardGqService boardGqService;

	// ???????????????
	@Override
	@RequestMapping(value = "/goodsForm.do", method = { RequestMethod.POST, RequestMethod.GET })
	protected ModelAndView viewForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");// ????????????????????? ???????????????
		/* String viewName = (String)request.getAttribute("viewName"); ????????????????????? */
		ModelAndView mav = new ModelAndView(viewName);
		mav.setViewName(viewName);
		return mav;
	}

	@Override
	@RequestMapping(value = "/addNewGoods.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView addNewGoods(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = multipartRequest.getSession();
		// ????????? ?????????????????? sellerInfo??? ?????????
		SellerVO sellerVO = (SellerVO) session.getAttribute("sellerInfo");
		if (sellerVO == null) {
			String viewName = "redirect:/main/main.do";
			String message = "???????????? ????????? ????????????.";
			mav.addObject("message", message);
			mav.setViewName(viewName);
			return mav;
		}
		String imageFileName = null;

		HashMap<String, Object> newGoodsMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		// input type=file?????? ?????? ?????????
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			System.out.println("name + value : " + name + " + " + value);
			newGoodsMap.put(name, value);
		}

		String H = (String) sellerVO.getS_HACCP_Num();
		String GR = (String) sellerVO.getS_GR_Num();
		System.out.println("H : " + H);
		System.out.println("GR : " + GR);
		logger.debug("===================================");
		logger.debug("H : " + H);
		logger.debug("GR : " + GR);

		logger.debug("===================================");
		if (H != null && GR != null) {
			newGoodsMap.put("g_cate3", "HGR");

		} else if (H != null && GR == null) {

			newGoodsMap.put("g_cate3", "H");

		} else if (H == null && GR != null) {
			newGoodsMap.put("g_cate3", "GR");
		} else {
			newGoodsMap.put("g_cate3", "N");

		}
		System.out.println("g_cate3 : " + (String) newGoodsMap.get("g_cate3"));

		// newGoodsMap????????? ???g_name??? ???????????? A??? ?????????
		String A = (String) newGoodsMap.get("g_name");
		// ???????????? A??? sellerInfo??? ???????????? s_Wname??? ????????? g_name??? ?????????
		String g_name = "[" + sellerVO.getS_Wname() + "]" + " " + A;
		System.out.println("g_name : " + g_name);
		// ???????????? g_name ?????? ???g_name??? ??????????????? newGoodsMap??? ?????????
		newGoodsMap.put("g_name", g_name);
		// ????????? ?????? ????????? = ????????????????????????
		goodsService.addNewGoods(newGoodsMap);
		// ????????? ??????????????? ????????? ?????? ???????????? ?????? ?????????INFO??? ?????????

		GoodsVO goodsInfo = (GoodsVO) goodsService.findg_id(g_name);
		int g_id = (Integer) goodsInfo.getG_id();

		// ????????? ??????????????? ???????????? ????????? ????????? C\Meal\Image\temp??? ???????????????
		List<HashMap<String, Object>> imageFileList = (List<HashMap<String, Object>>) upload(multipartRequest);
		// ????????? ???????????? ????????? ???????????? MAP??? ????????? ???????????? ????????????
		// ????????? ????????? ?????? ?????????
		try {
			if (imageFileList != null && imageFileList.size() != 0) {

				// HashMap??? ??????????????? ??????
				for (HashMap<String, Object> item : imageFileList) {

					item.put("g_id", g_id);
					goodsService.addGoodsImg(item);
					imageFileName = (String) item.get("fileName");
					String cate = (String) item.get("cate");
					if (!(imageFileName.equals("fileName") || imageFileName == null)) {
						// ???????????? ???????????? ????????? DB??? ?????? s_id | cate = fileName |fileName = originalfileName
						// temp??? ?????? ????????????????????? ??????
						File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + imageFileName);
						// ??????????????? ?????? ????????? ???????????? ??????
						File destDir = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "goods" + "\\" + g_id + "\\" + cate);
						// ?????? ??????? ????????? ???
						// ??????
						FileUtils.moveFileToDirectory(srcFile, destDir, true);
					}
				}
			}
			// ???????????? ??????????????? ?????? ????????? ????????? ????????????
			String message = "??????????????? ?????????????????????.";
			mav.addObject("goodsInfo", goodsInfo);
			String viewName = "redirect:/main/main.do";
			mav.addObject("message", message);
			mav.setViewName(viewName);
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			// ????????? ????????? temp??? ?????? ???????????? ????????? ???????????? ?????? ??????
			if (imageFileList != null && imageFileList.size() != 0) {
				for (HashMap<String, Object> item : imageFileList) {
					imageFileName = (String) item.get("fileName");
					File srcFile = new File(
							CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + "g_name" + "\\" + imageFileName);
					srcFile.delete();
				}
			}
			String viewName1 = (String) multipartRequest.getAttribute("viewName1");
			mav.setViewName(viewName1);
			return mav;
		}
	}

	// G_NAME??? ????????? ??????????????? ??????(???????????? ???????????? X)
	@Override
	@RequestMapping(value = "/goodsoverlapped.do", method = RequestMethod.POST)
	public ResponseEntity goodsoverlapped(@RequestParam("g_name") String g_name, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ResponseEntity resEntity = null;
		HttpSession session = request.getSession();
		SellerVO sellerVO = (SellerVO) session.getAttribute("sellerInfo");

		String g_name1 = "[" + sellerVO.getS_Wname() + "] " + g_name;
		String result = goodsService.goodsoverlapped(g_name1);

		resEntity = new ResponseEntity(result, HttpStatus.OK);
		return resEntity;
	}

	@Override
	@RequestMapping(value = "/goodsDetail.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView goodsDetail(@RequestParam("g_id") int g_id,
			@RequestParam(value = "message", required = false) String message,
			@RequestParam(value = "dateMap", required = false) Map<String, Object> dateMap,
			@RequestParam(value = "section", required = false) String section,
			@RequestParam(value = "pageNum", required = false) String pageNum, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();

		String viewName = (String) request.getAttribute("viewName");
		// ????????? ?????? ?????? ??? ?????????
		GoodsVO goodsInfo = (GoodsVO) goodsService.selectGoodsDetail(g_id);
		List<Img_gVO> imgList = (List<Img_gVO>) goodsService.selectImgList(g_id);
		// ????????????????????? ????????? ??????
		HashMap<String, Object> Map = new HashMap<String, Object>();
		Map.put("pageNum", pageNum);
		Map.put("section", section);
		HashMap<String, Object> pagingMap = (HashMap<String, Object>) paging(Map);
		pagingMap.put("g_id", g_id);

		List<BoardGrVO> boardGrList = boardGrService.selectGoodsBoardGrList(pagingMap);
		List<BoardGrVO> boardGr = boardGrService.selectGoodsBoardGrallList(g_id);
		List<BoardGrVO> boardGrReviewList = boardGrService.SelectReview(g_id);
		int g_avg = boardGrService.selectGoodAvg(g_id);

		for (BoardGrVO item : boardGr) {
			for (BoardGrVO j : boardGrList) {
				if (!((int) item.getB_gr_id() == (int) j.getParentNo())) {
					String compare = "N";
					item.setCompare(compare);
				} else {
					String compare = "Y";
					item.setCompare(compare);
					System.out.println("BoardCompare" + item.getB_gr_id());

					break;
				}
			}
		}
		// ????????????????????? ????????? ??????
		List<BoardGqVO> boardGqList = boardGqService.selectGoodsBoardGqList(pagingMap);
		List<BoardGqVO> boardGq = boardGqService.selectGoodsBoardGqallList(g_id);
		List<BoardGqVO> boardGqReviewList = boardGqService.SelectReview(g_id);

		for (BoardGqVO item : boardGq) {
			for (BoardGqVO j : boardGqList) {
				if (!((int) item.getB_gq_id() == (int) j.getParentNo())) {
					String compare = "N";
					item.setCompare(compare);
				} else {
					String compare = "Y";
					item.setCompare(compare);
					System.out.println("BoardCompare" + item.getB_gq_id());
					break;
				}
			}
		}

		HttpSession session = request.getSession();

		logger.info("--------------------------------");
		logger.info("goodsInfo" + goodsInfo);
		logger.info("--------------------------------");
		logger.info("imgList" + imgList);
		logger.info("--------------------------------");
		logger.info("boardGrList" + boardGrList);
		logger.info("--------------------------------");
		logger.info("boardGqList" + boardGqList);
		logger.info("--------------------------------");
		if (g_id == 0) {
			message = "????????? ???????????????.";
			mav.addObject("message", message);

			String viewName1 = "redirect:/main/main.do";
			mav.setViewName(viewName1);
		} else {
			mav.addObject("goodsInfo", goodsInfo);
			mav.addObject("g_avg", g_avg);
			mav.addObject("ImgList", imgList);
			mav.addObject("boardGrList", boardGrList);
			mav.addObject("boardGrReviewList", boardGrReviewList);
			mav.addObject("boardGqList", boardGqList);
			mav.addObject("boardGqReviewList", boardGqReviewList);
			mav.setViewName(viewName);
			addGoodsInQuick(g_id, goodsInfo, session);
		}
		if(message != null) {
			mav.addObject(message);
		}
		return mav;

	}

	// ?????? ????????? ?????? ?????????
	@Override
	@RequestMapping(value = "/selectGoodsPage.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView selectAllGoods(@RequestParam(value = "dateMap", required = false) Map<String, Object> dateMap,
			@RequestParam(value = "section1", required = false) String section,
			@RequestParam(value = "pgNum", required = false) String pgNum, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		SellerVO sellerVO = (SellerVO) session.getAttribute("sellerInfo");
		if (sellerVO == null) {
			String message = "????????? ???????????? ????????????.";
			mav.addObject("message", message);
			String viewName = "redirect:/main/main.do";
			mav.setViewName(viewName);
			return mav;
		}
		String s_id = sellerVO.getS_id();
		String viewName = (String) request.getAttribute("viewName");
		mav.setViewName(viewName);
		//
		HashMap<String, Object> pagingInfo = new HashMap<String, Object>();
		pagingInfo.put("section", section);
		pagingInfo.put("pageNum", pgNum);

		HashMap<String, Object> pagingMap = (HashMap<String, Object>) paging(pagingInfo);

		pagingMap.put("s_id", s_id);
		List<GoodsVO> goodsList = goodsService.selectGoodsPage(pagingMap);
		mav.addObject("goodsList", goodsList);

		return mav;
	}

	@Override
	@RequestMapping(value = "/updateGoodsForm.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView updateGoodsForm(@RequestParam("g_id") int g_id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		SellerVO sellerInfo = (SellerVO) session.getAttribute("sellerInfo");
		// AdminVO adminInfo = (AdminVO) session.getAttribute("AdminInfo");
		GoodsVO goodsInfo = (GoodsVO) goodsService.goodsG_Info(g_id);
		List<Img_gVO> imageList = (List<Img_gVO>) goodsService.selectImgList(g_id);

		if (sellerInfo.getS_id().equals(goodsInfo.getS_id())) {
			String viewName = "/goods/updateGoodsForm";
			mav.addObject("goodsInfo", goodsInfo);
			mav.setViewName(viewName);
			mav.addObject("imageList", imageList);
			return mav;

		} // else if (adminInfo != null) {
			// String viewName = "/goods/updateGoodsForm";
			// mav.setViewName(viewName);
			// return mav;

		// }
		else {
			String message = "??????????????? ???????????? ????????????.";
			mav.addObject("message", message);
			String viewName = "redirect:/goods/searchGoods.do";
			mav.setViewName(viewName);
			return mav;
		}
	}

	@Override
	@RequestMapping(value = "/updateGoods.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity updateGoods(@RequestParam("g_id") int g_id, MultipartHttpServletRequest multipartRequest,
			HttpServletResponse response) throws Exception {
		HttpSession session = multipartRequest.getSession();
		GoodsVO goodsInfo = (GoodsVO) goodsService.goodsG_Info(g_id);
		HashMap<String, Object> newGoodsMap = new HashMap<String, Object>();

		Enumeration enu = multipartRequest.getParameterNames();
		// input type=file?????? ?????? ?????????
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			System.out.println("name + value : " + name + value);
			System.out.println("value.class.name = " + value.getClass().getName());
			if (value != null && value != "") {
				newGoodsMap.put(name, value);
			}
		}

		String g_saleDate1 = (String) newGoodsMap.get("g_saleDate1");
		String g_saleDate2 = (String) newGoodsMap.get("g_saleDate2");
		String g_saleDate3 = (String) newGoodsMap.get("g_saleDate3");
		String g_saleDate4 = (String) newGoodsMap.get("g_saleDate4");

		
		if (g_saleDate3.contains("-") ) {
			newGoodsMap.put("g_saleDate1", g_saleDate3);
			logger.info("==============================");
			logger.info("if1 : in");
			logger.info("==============================");
			
		} else if(!g_saleDate1.equals("")){
			DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
			
			String beforeDate1 = g_saleDate1;
			Date date1 = dateFormat.parse(beforeDate1);
			String afterDate1 = sdf.format(date1);
			newGoodsMap.put("g_saleDate1", afterDate1);
			logger.info("==============================");
			logger.info("g_saleD1 : "+ afterDate1);
			logger.info("g_saleD3 : "+ g_saleDate3);
			logger.info("g_saleD4 : "+ g_saleDate4);
			logger.info("if1 : "+newGoodsMap.containsKey("g_saleDate3"));
			logger.info("if1 : "+newGoodsMap.containsKey("g_saleDate4"));
			logger.info("if1 : "+newGoodsMap.containsValue("g_saleDate3"));
			logger.info("if2 : "+newGoodsMap.containsValue("g_saleDate4"));
			logger.info("==============================");
			
		}
		if (g_saleDate4.contains("-")) {
			newGoodsMap.put("g_saleDate2", g_saleDate4);
			logger.info("==============================");
			logger.info("if2 : in");
			logger.info("==============================");
			
		} else if(!g_saleDate2.equals("")) {
			DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
			
			String beforeDate2 = g_saleDate2;
			Date date2 = dateFormat.parse(beforeDate2);
			String afterDate2 = sdf.format(date2);
			newGoodsMap.put("g_saleDate2", afterDate2);
			logger.info("==============================");
			logger.info("g_saleD2 : "+ afterDate2);
			logger.info("g_saleD3 : "+ g_saleDate3);
			logger.info("g_saleD4 : "+ g_saleDate4);
			logger.info("if1 : "+newGoodsMap.containsKey("g_saleDate3"));
			logger.info("if1 : "+newGoodsMap.containsKey("g_saleDate4"));
			logger.info("if1 : "+newGoodsMap.containsValue("g_saleDate3"));
			logger.info("if2 : "+newGoodsMap.containsValue("g_saleDate4"));
			logger.info("==============================");
		} 
		
		// 3.4??????
		/*
		 * if (g_saleDate3 != null || g_saleDate3 != "") {
		 * newGoodsMap.put("g_saleDate1", g_saleDate3); } if (g_saleDate4 != null ||
		 * g_saleDate4 != "") { newGoodsMap.put("g_saleDate2", g_saleDate4); }
		 */
		String message = null;
		ResponseEntity resEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			// ????????? ????????? ????????? g_id??????
			int g_id1 = (Integer) goodsInfo.getG_id();
			// ?????? ????????? ?????? ????????????????????? ?????????
			List<Img_gVO> OldimageList = (List<Img_gVO>) goodsService.selectImgList(g_id);
			List<HashMap<String, Object>> imageFileList = (List<HashMap<String, Object>>) upload(multipartRequest);
			// ?????? ???????????? ?????? ????????? ??????
			for (Img_gVO oldi : OldimageList) {
				for (HashMap<String, Object> newi : imageFileList) {
					String oldCate = oldi.getCate();
					String newCate = (String) newi.get("cate");
					String newImgFileName = (String) newi.get("fileName");
					String oldImgFileName = oldi.getFileName();
					newi.put("g_id", g_id);
					// ?????? ?????????????????? ????????? ????????? ????????? ?????? ????????????
					if (oldCate.equals(newCate)) {
						// ?????????????????? ?????? ??????????????? ????????? ????????? ??????
						if (oldImgFileName != null && newImgFileName != null) {
							String oldpath = CURR_IMAGE_UPLOAD_PATH + "\\" + "goods" + "\\" + g_id + "\\" + oldCate;
							deleteFolder(oldpath);
							// ????????? ????????? ????????????(upload)???????????? path????????? ?????? ?????????
							File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + newImgFileName);
							// ??????????????? ?????? ????????? ???????????? ??????
							File destDir = new File(
									CURR_IMAGE_UPLOAD_PATH + "\\" + "goods" + "\\" + g_id + "\\" + newCate);
							// ??????
							FileUtils.moveFileToDirectory(srcFile, destDir, true);
							// db??? ?????? ????????? ?????? ???????????????
							goodsService.updateGoodsImg(newi);
							System.out.println(newi);
							// ??????????????? ????????? ?????????
						} else if (oldImgFileName == null && newImgFileName != null) {
							// ????????? ????????? ????????????(upload)???????????? path????????? ?????? ?????????
							File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + newImgFileName);
							// ??????????????? ?????? ????????? ???????????? ??????
							File destDir = new File(
									CURR_IMAGE_UPLOAD_PATH + "\\" + "goods" + "\\" + g_id + "\\" + newCate);
							// ??????
							FileUtils.moveFileToDirectory(srcFile, destDir, true);
							// DB??? ??????
							goodsService.addGoodsImg(newi);
							System.out.println(newi);
						}
					}
				}
			}
			goodsService.updateGoods(newGoodsMap);

			message = "<script>";
			message += " alert('??????????????? ?????????????????????..');";
			// ???????????? ????????? ????????? ???????????? ????????? ??????????????? ????????? ??????
			message += " location.href='" + multipartRequest.getContextPath() + "/main/main.do';";
			message += " </script>";

		} catch (Exception e) {

			message = "<script>";
			message += " alert('?????? ????????? ??????????????????');";
			message += " location.href='" + multipartRequest.getContextPath() + "/main/main.do';";
			message += " </script>";
			e.printStackTrace();
		}
		resEntity = new ResponseEntity(message, responseHeaders, HttpStatus.OK);
		return resEntity;

	}

	@Override
	@RequestMapping(value = "/deleteGoods.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView deleteGoods(@RequestParam HashMap<String, Object> map, @RequestParam("g_id") int g_id,
			HttpServletRequest request, HttpServletResponse repsponse) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		SellerVO sellerInfo = (SellerVO) session.getAttribute("sellerInfo");
		GoodsVO goodsInfo = (GoodsVO) goodsService.goodsG_Info(g_id);
		String s_id = (String) sellerInfo.getS_id();
		String s_id1 = (String) goodsInfo.getS_id();

		if (s_id.equals(s_id1)) {
			String path = CURR_IMAGE_UPLOAD_PATH + "\\" + "goods" + "\\" + goodsInfo.getG_id();
			deleteFolder(path);
			goodsService.deleteGoods(goodsInfo);

			session.setAttribute("isLogOn", true);
			String message = "????????? ?????????????????????.";
			mav.addObject("message", message);
			String viewName = "redirect:/goods/selectGoodsPage.do";
			mav.setViewName(viewName);
			return mav;
		} else {
			String viewName = "redirect:/goods/selectGoodsPage.do";
			mav.setViewName(viewName);
			return mav;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/saleEnd.do", method = { RequestMethod.POST, RequestMethod.GET })
	public String goodsSaleEnd(int g_id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("UTF-8");
		try {
			goodsService.goodsSaleEnd(g_id);
			String message = "??????????????? ??????????????? ?????????????????????.";
			return message;
		} catch (Exception e) {
			String message = "??????????????? ????????????.";
			return message;
		}
	}

	// ????????? ????????? ??? ????????? ??????
	@Override
	public void addGoodsInQuick(int g_id, GoodsVO goodsInfo, HttpSession session) throws Exception {
		boolean already_existed = false;
		List<GoodsVO> quickGoodsList;
		quickGoodsList = (ArrayList<GoodsVO>) session.getAttribute("quickGoodsList");

		if (quickGoodsList != null) {
			if (quickGoodsList.size() < 4) {
				for (int i = 0; i < quickGoodsList.size(); i++) {
					GoodsVO _goodsBean = (GoodsVO) quickGoodsList.get(i);
					if (g_id == _goodsBean.getG_id()) {
						already_existed = true;
						break;
					}
				}
				if (already_existed == false) {
					quickGoodsList.add(goodsInfo);
				}
			}

		} else {
			quickGoodsList = new ArrayList<GoodsVO>();
			quickGoodsList.add(goodsInfo);

		}
		session.setAttribute("quickGoodsList", quickGoodsList);
		session.setAttribute("quickGoodsListNum", quickGoodsList.size());
	}

}