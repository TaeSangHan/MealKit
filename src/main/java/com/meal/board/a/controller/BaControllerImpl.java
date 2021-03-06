package com.meal.board.a.controller;

import java.io.File;
import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.meal.admin.vo.AdminVO;
import com.meal.board.a.service.BaService;
import com.meal.board.a.vo.BaVO;
import com.meal.board.a.vo.Img_aVO;
import com.meal.board.one.vo.Board1VO;
import com.meal.common.controller.BaseController;

@Controller("boardAController")
@RequestMapping("/boardA")
public class BaControllerImpl extends BaseController implements BaController {
	private String CURR_IMAGE_UPLOAD_PATH = "C:\\Meal\\Image";
	
	@Autowired
	private BaService baService;
	
	@Override
	@RequestMapping(value = "/boardAinsert.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView writeBoardA(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		String imageFileName = null;
		HttpSession session = multipartRequest.getSession();
		

		HashMap<String, Object> newboardAMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			System.out.println("name + value = " + name + " : " + value);
			newboardAMap.put(name, value);
		}
		baService.boardAWrite(newboardAMap);

		// auto??? ???????????? b_a_id ??????
		BaVO boardInfo = (BaVO) baService.findb_a_id();
		int b_a_id = (Integer) boardInfo.getB_a_id();
		AdminVO adminInfo = (AdminVO)session.getAttribute("adminInfo");
		if (adminInfo == null) {
			String viewName = "redirect:/main/main.do";
			String message = "???????????? ????????? ????????????.";
			mav.addObject("message", message);
			mav.setViewName(viewName);
			return mav;
		}

		List<HashMap<String, Object>> imageFileList = (List<HashMap<String, Object>>) upload(multipartRequest);
		// ????????? ????????? ?????? ?????????
		try {
			if (imageFileList != null && imageFileList.size() != 0) {
				for (HashMap<String, Object> item : imageFileList) {
					System.out.println("????????????");
					// ????????? ???????????? ????????? ???????????? MAP??? ????????? ???????????? ????????????
					item.put("b_a_id", b_a_id);
					baService.addImg(item);
					// ????????? ????????????????????? ???????????? ??????
					imageFileName = (String) item.get("fileName");
					String cate = (String) item.get("cate");
					if (!(imageFileName.equals("fileName") || imageFileName == null)) {
						// ???????????? ???????????? ????????? DB??? ?????? b_a_id | fileName = originalfileName
						// temp??? ?????? ????????????????????? ??????
						File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + imageFileName);
						System.out.println("????????????");
						//??????????????? ?????? ????????? ???????????? ??????
						File destDir = new File(
								CURR_IMAGE_UPLOAD_PATH + "\\" + "boardA" + "\\"  + b_a_id + "\\" + cate);
						// ??????
						FileUtils.moveFileToDirectory(srcFile, destDir, true);
					}
				}
			}

			String message = "???????????? ?????????????????????.";
			mav.addObject("adminInfo", adminInfo);
			String viewName = "redirect:/main/main.do";
			mav.addObject("message", message);
			mav.setViewName(viewName);
			return mav;
		} catch (Exception e) {
			System.out.println("??????");
			e.printStackTrace();
			// ????????? ????????? temp??? ?????? ???????????? ????????? ???????????? ?????? ??????
			if (imageFileList != null && imageFileList.size() != 0) {
				for (HashMap<String, Object> item : imageFileList) {
					imageFileName = (String) item.get("fileName");
					File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + "\\" + imageFileName);
					srcFile.delete();
					String viewName1 = "redirect:/main/main.do";
					mav.setViewName(viewName1);
					return mav;
				}
			}
			return mav;
		}
	}
	
	@Override
	@RequestMapping(value="/boardAWrite.do", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView boardAWrite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		AdminVO adminInfo = (AdminVO) session.getAttribute("adminInfo");
		System.out.println("-----------------------");
		System.out.println("adminInfo : " + adminInfo);
		System.out.println("-----------------------");
		
		if (adminInfo == null) {
			String viewName1 = "redirect:/main/main.do";
			String message = "????????? ???????????????.";
			mav.addObject("message", message);
			mav.setViewName(viewName1);
			return mav;
		} else {
			String viewName = "/boardA/boardAWrite";
			mav.setViewName(viewName);
			return mav;
		}
	}
	
	@Override
	@RequestMapping(value="/boardAList.do", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView boardAList(@RequestParam(value = "dateMap", required = false) Map<String, Object> dateMap,
		@RequestParam(value = "section1", required = false) String section,
		@RequestParam(value = "pgNum", required = false) String pgNum, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		AdminVO adminInfo = (AdminVO) session.getAttribute("adminInfo");
		
		if (adminInfo != null) {
			
			String a_id = adminInfo.getA_id();
			System.out.println("-------------");
			System.out.println(a_id);
			System.out.println("-------------");
			String viewName = (String) request.getAttribute("viewName");
			mav.setViewName(viewName);
			
			
			HashMap<String, Object> pagingInfo = new HashMap<String, Object>();
			pagingInfo.put("section", section);
			pagingInfo.put("pgNum", pgNum);
			HashMap<String, Object> pagingMap = (HashMap<String, Object>) paging(pagingInfo);
		
			
			//????????? ????????? ????????? ?????? ??????
			List<BaVO> boardAList = (List<BaVO>) baService.selectBAlist(pagingMap);
			System.out.println("---------------------------");
			System.out.println("boardAList : " + boardAList);
			System.out.println("---------------------------");

		
			mav.addObject("boardAList", boardAList);
			mav.addObject("adminInfo", adminInfo);
			mav.setViewName(viewName);

			return mav;
		
			} else {
				String message ="????????? ?????????????????????.";
				String viewName1 = "redirect:/admin/selectAllMembers.do";
				mav.addObject("message", message);
				mav.setViewName(viewName1);
				return mav;
			}
}

	@Override
	@RequestMapping(value="/boardADetail.do", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView boardADetail(@RequestParam("b_a_id") String b_a_id, @RequestParam("cate") String cate, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		AdminVO adminInfo = (AdminVO) session.getAttribute("adminInfo");
		
		List<Img_aVO> imgList =(List<Img_aVO>)baService.selectImgList(b_a_id); 
		BaVO boardAInfo = (BaVO) baService.selectBaDetail(b_a_id);

		String viewName = (String) request.getAttribute("viewName");
		mav.addObject("cate", cate);
		mav.addObject("adminInfo", adminInfo);
		mav.addObject("imgList", imgList);
		mav.addObject("boardAInfo", boardAInfo);

		if ( cate.equals("?????????") ) {
			mav.setViewName(viewName);
			
			return mav;
			} else if ( cate.equals("????????????")) {
			mav.setViewName(viewName);
			
			return mav;
			} else if (cate.equals("??????????????????")) {
			mav.setViewName(viewName);
					            
			return mav;
			} 
		return mav;
	
	}
	
	@Override
	   @RequestMapping(value="/boardASPList.do", method= {RequestMethod.GET, RequestMethod.POST})
	   public ModelAndView boardASPList(@RequestParam(value = "dateMap", required = false) Map<String, Object> dateMap,
	      @RequestParam(value = "section1", required = false) String section,
	      @RequestParam(value = "pgNum", required = false) String pgNum,
	      @RequestParam(value = "cate", required = false) String cate,
	      @RequestParam(value = "b_a_id", required = false) String b_a_id,
	      HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	      ModelAndView mav = new ModelAndView();
	      HttpSession session = request.getSession();
	      
	      HashMap<String, Object> pagingInfo = new HashMap<String, Object>();
	      pagingInfo.put("section", section);
	      pagingInfo.put("pgNum", pgNum);
	      HashMap<String, Object> pagingMap = (HashMap<String, Object>) paging(pagingInfo);

	      
	      List<BaVO> boardAList = (List<BaVO>) baService.selectBAlist(pagingMap);
	      mav.addObject("cate", cate);
	      mav.addObject("boardAList", boardAList);


	    	 if ( cate.equals("?????????") ) {
	    		 mav.setViewName("/boardA/boardASPList2");

	         return mav;
	         
	    	 } else if ( cate.equals("????????????")) {
	        	 mav.setViewName("/boardA/boardASPList1");

	            return mav;
	         
	    	 } else if (cate.equals("??????????????????")) {
	        	 mav.setViewName("/boardA/boardASPList3");
	            
	        	return mav;
	         
	    	 } 
	    	 
	    	 return mav;
	      }
	
	@Override
	@RequestMapping(value = "/UpdateBAform.do", method = { RequestMethod.POST, RequestMethod.GET})
	public ModelAndView boardAUpdateform(@RequestParam("b_a_id") String b_a_id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		AdminVO adminVO = (AdminVO) session.getAttribute("adminInfo");
		System.out.println("==========?????????"+adminVO);
		List<Img_aVO> imgList =(List<Img_aVO>)baService.selectImgList(b_a_id);
		System.out.println("==========??????"+imgList);
		BaVO boardAInfo = (BaVO) baService.selectBaDetail(b_a_id);
		System.out.println("==========?????????"+boardAInfo);
		
			if (adminVO != null) {
				String viewName = "/boardA/UpdateBAform";
				
				mav.addObject("b_a_id", b_a_id);
				mav.addObject("adminInfo", adminVO);
				mav.addObject("boardAInfo", boardAInfo);
				mav.addObject("imgList", imgList);
				mav.setViewName(viewName);
				return mav;
			} else {
				String message = "??????????????? ???????????? ????????????.";

				mav.addObject("message", message);
				mav.addObject("b_a_id", b_a_id);
				String viewName = "/boardA/boardAList.do";
				mav.setViewName(viewName);
				return mav;
			}
	}
	
	@RequestMapping(value = "/updateBA.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity updateBA(@RequestParam("b_a_id") String b_a_id, MultipartHttpServletRequest multipartRequest,
			HttpServletResponse response) throws Exception {
		HttpSession session = multipartRequest.getSession();
		BaVO boardAInfo = (BaVO) baService.selectBaDetail(b_a_id);
		HashMap<String, Object> newBaMap = new HashMap<String, Object>();

		Enumeration enu = multipartRequest.getParameterNames();
		// input type=file?????? ?????? ?????????
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			System.out.println("name + value : " + name + value);
			System.out.println("value.class.name = " + value.getClass().getName());
			if (value != null && value != "") {
				newBaMap.put(name, value);
			}
		} 
		
		String message = null;
		ResponseEntity resEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			// ????????? ????????? ????????? g_id??????
			int b_a_id1 = boardAInfo.getB_a_id();
			// ?????? ????????? ?????? ????????????????????? ?????????
			List<Img_aVO> OldimageList = (List<Img_aVO>) baService.selectImgList(b_a_id);
			List<HashMap<String, Object>> imageFileList = (List<HashMap<String, Object>>) upload(multipartRequest);
			// ?????? ???????????? ?????? ????????? ??????
			for (Img_aVO oldImgList : OldimageList) {
				for (HashMap<String, Object> newImgList : imageFileList) {
					String oldCate = oldImgList.getCate();
					String newCate = (String) newImgList.get("cate");
					String newImgFileName = (String) newImgList.get("fileName");
					String oldImgFileName = oldImgList.getFileName();
					newImgList.put("b_a_id", b_a_id);
					// ?????? ?????????????????? ????????? ????????? ????????? ?????? ????????????
					if (oldCate.equals(newCate)) {
						// ?????????????????? ?????? ??????????????? ????????? ????????? ??????
						if (oldImgFileName != null && newImgFileName != null) {
							String oldpath = CURR_IMAGE_UPLOAD_PATH + "\\" + "boardA" + "\\" + b_a_id + "\\" + oldCate;
							deleteFolder(oldpath);
							// ????????? ????????? ????????????(upload)???????????? path????????? ?????? ?????????
							File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + newImgFileName);
							// ??????????????? ?????? ????????? ???????????? ??????
							File destDir = new File(
									CURR_IMAGE_UPLOAD_PATH + "\\" + "boardA" + "\\" + b_a_id + "\\" + newCate);
							// ??????
							FileUtils.moveFileToDirectory(srcFile, destDir, true);
							// db??? ?????? ????????? ?????? ???????????????
							baService.updateBAImg(newImgList);
							System.out.println(newImgList);
							// ??????????????? ????????? ?????????
						} else if (oldImgFileName == null && newImgFileName != null) {
							// ????????? ????????? ????????????(upload)???????????? path????????? ?????? ?????????
							File srcFile = new File(CURR_IMAGE_UPLOAD_PATH + "\\" + "temp" + "\\" + newImgFileName);
							// ??????????????? ?????? ????????? ???????????? ??????
							File destDir = new File(
									CURR_IMAGE_UPLOAD_PATH + "\\" + "boardA" + "\\" + b_a_id + "\\" + newCate);
							// ??????
							FileUtils.moveFileToDirectory(srcFile, destDir, true);
							// DB??? ??????
							baService.addImg(newImgList);
							System.out.println(newImgList);
						}
					}
				}
			}
			baService.updateBAImg(newBaMap);

			message = "<script>";
			message += " alert('??????????????? ?????????????????????..');";
			// ???????????? ????????? ????????? ???????????? ????????? ??????????????? ????????? ??????
			message += " location.href='" + multipartRequest.getContextPath() + "/boardA/boardAList.do';";
			message += " </script>";

		} catch (Exception e) {

			message = "<script>";
			message += " alert('?????? ????????? ??????????????????');";
			message += " location.href='" + multipartRequest.getContextPath() + "/boardA/boardAList.do';";
			message += " </script>";
			e.printStackTrace();
		}
		resEntity = new ResponseEntity(message, responseHeaders, HttpStatus.OK);
		return resEntity;

	}
			
	@Override
	@RequestMapping(value = "/deleteBA.do", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView deleteBA(@RequestParam HashMap<String, Object> map, @RequestParam("b_a_id") String b_a_id,
			HttpServletRequest request, HttpServletResponse repsponse) throws Exception {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		System.out.println("==================??????" + session);
		AdminVO adminInfo = (AdminVO) session.getAttribute("adminInfo");
		System.out.println("==================?????????" + adminInfo);
		BaVO boardAInfo = (BaVO) baService.selectBaDetail(b_a_id);
		System.out.println("==================?????????" + boardAInfo);
		String a_id = adminInfo.getA_id();
		String a_id1 = boardAInfo.getA_id();

		if (a_id.equals(a_id1)) {
			String path = CURR_IMAGE_UPLOAD_PATH + "\\" + "boardA" + "\\" + boardAInfo.getB_a_id();
			deleteFolder(path);
			baService.deleteBA(boardAInfo);

			session.setAttribute("isLogOn", true);
			String message = "????????? ?????????????????????.";
			String viewName = "redirect:/boardA/boardAList.do";
			
			mav.addObject("adminInfo", adminInfo);
			mav.addObject("boardAInfo", boardAInfo);
			mav.addObject("message", message);
			mav.setViewName(viewName);
			return mav;
		} else {
			String message = "????????? ???????????????.";
			String viewName = "redirect:/boardA/boardAList.do";
			mav.addObject("message",message);
			mav.setViewName(viewName);
			return mav;
		}
	}

}
