package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.*;

/**
 * Controller chính xử lý tất cả các chức năng CRUD cho Phòng ban
 * Gộp các servlet: dsphongban, themPhongBan, xoaPhongBan, locPhongBan
 */
public class PhongBanController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            // Mặc định: hiển thị danh sách phòng ban
            hienThiDanhSachPhongBan(request, response);
        } else {
            switch (action) {
                case "search":
                    timKiemPhongBan(request, response);
                    break;
                case "detail":
                    xemChiTietPhongBan(request, response);
                    break;
                default:
                    hienThiDanhSachPhongBan(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            // Kiểm tra xem có phải là edit không (có deptId)
            String deptId = request.getParameter("deptId");
            if (deptId != null && !deptId.trim().isEmpty()) {
                capNhatPhongBan(request, response);
            } else {
                themPhongBan(request, response);
            }
        } else {
            switch (action) {
                case "add":
                    themPhongBan(request, response);
                    break;
                case "edit":
                    capNhatPhongBan(request, response);
                    break;
                case "delete":
                    xoaPhongBan(request, response);
                    break;
                case "search":
                    timKiemPhongBan(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ");
            }
        }
    }

    /**
     * Hiển thị danh sách phòng ban
     */
    private void hienThiDanhSachPhongBan(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        List<Map<String, Object>> danhSach = new ArrayList<>();

        try {
            KNCSDL kn = new KNCSDL();
            ResultSet rs = kn.layDanhSachPhongBan();

            while (rs.next()) {
                Map<String, Object> pb = new HashMap<>();
                pb.put("id", rs.getString("id"));
                pb.put("ten_phong", rs.getString("ten_phong"));
                pb.put("truong_phong_id", rs.getString("truong_phong_id"));
                pb.put("ten_truong_phong", rs.getString("ten_truong_phong"));
                pb.put("so_nhan_vien", rs.getString("so_nhan_vien"));
                pb.put("ngay_tao", rs.getString("ngay_tao"));
                danhSach.add(pb);
            }

            rs.close();

            // Gửi dữ liệu sang JSP
            request.setAttribute("danhSach", danhSach);
            request.getRequestDispatcher("department.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h3 style='color:red'>❌ Lỗi: " + e.getMessage() + "</h3>");
        }
    }

    /**
     * Thêm phòng ban mới
     */
    private void themPhongBan(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        PrintWriter out = response.getWriter();

        try {
            String tenPhong = request.getParameter("ten_phong");
            String truongPhongIdStr = request.getParameter("truong_phong_id");
            
            Integer truongPhongId = null;
            if (truongPhongIdStr != null && !truongPhongIdStr.trim().isEmpty()) {
                truongPhongId = Integer.parseInt(truongPhongIdStr);
            }

            KNCSDL kn = new KNCSDL();
            boolean result = kn.themPhongBan(tenPhong, truongPhongId);

            out.print(result ? "ok" : "error");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("error");
        }
    }

    /**
     * Cập nhật phòng ban
     */
    private void capNhatPhongBan(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String id = request.getParameter("deptId");
        String tenPhong = request.getParameter("ten_phong");
        String truongPhongId = request.getParameter("truong_phong_id");

        try {
            KNCSDL kn = new KNCSDL();
            boolean success = kn.capNhatPhongBan(Integer.parseInt(id), tenPhong, 
                truongPhongId != null && !truongPhongId.isEmpty() ? Integer.parseInt(truongPhongId) : null);

            if (success) {
                response.getWriter().write("{\"status\":\"ok\"}");
            } else {
                response.setStatus(500);
                response.getWriter().write("{\"status\":\"error\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"status\":\"error\", \"message\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Xóa phòng ban
     */
    private void xoaPhongBan(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String id = request.getParameter("id");
        String phongBanMoiId = request.getParameter("phongBanMoi");

        try {
            KNCSDL kn = new KNCSDL();
            
            // Kiểm tra phòng ban có nhân viên không
            boolean coNhanVien = kn.kiemTraPhongBanCoNhanVien(Integer.parseInt(id));
            
            if (coNhanVien) {
                if (phongBanMoiId == null || phongBanMoiId.trim().isEmpty()) {
                    // Trả về thông báo cần chuyển nhân viên
                    response.getWriter().write("{\"status\":\"need_transfer\", \"message\":\"Phòng ban còn có nhân viên, cần chuyển sang phòng khác trước khi xóa\"}");
                    return;
                } else {
                    // Chuyển nhân viên sang phòng mới
                    boolean chuyenThanhCong = kn.chuyenNhanVienSangPhongBanKhac(Integer.parseInt(id), Integer.parseInt(phongBanMoiId));
                    if (!chuyenThanhCong) {
                        response.setStatus(500);
                        response.getWriter().write("{\"status\":\"error\", \"message\":\"Không thể chuyển nhân viên\"}");
                        return;
                    }
                }
            }
            
            // Xóa phòng ban
            boolean success = kn.xoaPhongBan(Integer.parseInt(id));

            if (success) {
                response.getWriter().write("{\"status\":\"ok\"}");
            } else {
                response.setStatus(500);
                response.getWriter().write("{\"status\":\"error\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Tìm kiếm phòng ban
     */
    private void timKiemPhongBan(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String keyword = request.getParameter("keyword");

        try (PrintWriter out = response.getWriter()) {
            KNCSDL kn = new KNCSDL();
            List<Map<String, Object>> danhSach = kn.locPhongBan(keyword);

            if (danhSach != null && !danhSach.isEmpty()) {
                for (Map<String, Object> pb : danhSach) {
                    out.println("<tr>");
                    out.println("<td>" + pb.get("id") + "</td>");
                    out.println("<td class='fw-semibold'>" + pb.get("ten_phong") + "</td>");
                    out.println("<td>" + (pb.get("ten_truong_phong") != null ? pb.get("ten_truong_phong") : "<span class='text-muted'>Chưa có</span>") + "</td>");
                    out.println("<td><span class='badge bg-info'>" + pb.get("so_nhan_vien") + " người</span></td>");
                    out.println("<td>" + pb.get("ngay_tao") + "</td>");
                    out.println("<td class='action-btns'>");
                    out.println("<button class='btn btn-sm btn-primary detail-dept-btn' data-id='" + pb.get("id") + "'>");
                    out.println("<i class='fa-solid fa-eye'></i></button>");
                    out.println("<button class='btn btn-sm btn-warning edit-dept-btn' data-id='" + pb.get("id") + "'"
                            + " data-name='" + pb.get("ten_phong") + "'"
                            + " data-manager='" + (pb.get("truong_phong_id") != null ? pb.get("truong_phong_id") : "") + "'>");
                    out.println("<i class='fa-solid fa-pen'></i></button>");
                    out.println("<button class='btn btn-sm btn-danger delete-dept-btn' data-id='" + pb.get("id") + "'>");
                    out.println("<i class='fa-solid fa-trash'></i></button>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            } else {
                out.println("<tr><td colspan='6' class='text-center'>Không có dữ liệu phù hợp</td></tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='6' class='text-center text-danger'>Lỗi: " + e.getMessage() + "</td></tr>");
        }
    }

    /**
     * Xem chi tiết phòng ban
     */
    private void xemChiTietPhongBan(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String id = request.getParameter("id");

        try (PrintWriter out = response.getWriter()) {
            KNCSDL kn = new KNCSDL();
            Map<String, Object> phongBan = kn.layChiTietPhongBan(Integer.parseInt(id));
            List<Map<String, Object>> danhSachNhanVien = kn.layNhanVienTheoPhongBan(Integer.parseInt(id));

            if (phongBan != null) {
                out.println("<div class='row'>");
                out.println("<div class='col-md-6'>");
                out.println("<h6 class='text-primary mb-3'><i class='fa-solid fa-building me-2'></i>Thông tin phòng ban</h6>");
                out.println("<table class='table table-borderless'>");
                out.println("<tr><td class='fw-semibold'>ID:</td><td>" + phongBan.get("id") + "</td></tr>");
                out.println("<tr><td class='fw-semibold'>Tên phòng:</td><td>" + phongBan.get("ten_phong") + "</td></tr>");
                out.println("<tr><td class='fw-semibold'>Trưởng phòng:</td><td>" + 
                    (phongBan.get("ten_truong_phong") != null ? phongBan.get("ten_truong_phong") : "<span class='text-muted'>Chưa có</span>") + "</td></tr>");
                out.println("<tr><td class='fw-semibold'>Ngày tạo:</td><td>" + phongBan.get("ngay_tao") + "</td></tr>");
                out.println("<tr><td class='fw-semibold'>Số nhân viên:</td><td><span class='badge bg-info'>" + 
                    (danhSachNhanVien != null ? danhSachNhanVien.size() : 0) + " người</span></td></tr>");
                out.println("</table>");
                out.println("</div>");
                
                out.println("<div class='col-md-6'>");
                out.println("<h6 class='text-primary mb-3'><i class='fa-solid fa-users me-2'></i>Danh sách nhân viên</h6>");
                
                if (danhSachNhanVien != null && !danhSachNhanVien.isEmpty()) {
                    out.println("<div class='list-group list-group-flush' style='max-height: 300px; overflow-y: auto;'>");
                    for (Map<String, Object> nv : danhSachNhanVien) {
                        String badgeClass = "secondary";
                        if (nv.get("id").equals(phongBan.get("truong_phong_id"))) {
                            badgeClass = "warning";
                        }
                        
                        out.println("<div class='list-group-item d-flex justify-content-between align-items-center'>");
                        out.println("<div>");
                        out.println("<div class='fw-semibold'>" + nv.get("ho_ten") + "</div>");
                        out.println("<small class='text-muted'>" + nv.get("chuc_vu") + "</small>");
                        out.println("</div>");
                        out.println("<span class='badge bg-" + badgeClass + "'>" + 
                            (nv.get("id").equals(phongBan.get("truong_phong_id")) ? "Trưởng phòng" : "Nhân viên") + "</span>");
                        out.println("</div>");
                    }
                    out.println("</div>");
                } else {
                    out.println("<div class='alert alert-info mb-0'>");
                    out.println("<i class='fa-solid fa-info-circle me-2'></i>Phòng ban chưa có nhân viên nào.");
                    out.println("</div>");
                }
                out.println("</div>");
                out.println("</div>");
                
            } else {
                out.println("<div class='alert alert-danger'>");
                out.println("<i class='fa-solid fa-exclamation-triangle me-2'></i>Không tìm thấy thông tin phòng ban!");
                out.println("</div>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<div class='alert alert-danger'>Lỗi: " + e.getMessage() + "</div>");
        }
    }
}
