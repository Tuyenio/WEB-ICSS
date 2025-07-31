package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý các API calls cho Phòng ban (JSON responses)
 * Gộp các servlet: layNhanVienChoTruongPhong, layPhongBanKhac
 */
public class PhongBanApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action parameter");
            return;
        }
        
        switch (action) {
            case "employees":
                layNhanVienChoTruongPhong(request, response);
                break;
            case "other-departments":
                layPhongBanKhac(request, response);
                break;
            case "detail":
                layChiTietPhongBan(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action: " + action);
        }
    }

    /**
     * Lấy danh sách nhân viên cho dropdown trưởng phòng
     */
    private void layNhanVienChoTruongPhong(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            KNCSDL kn = new KNCSDL();
            ResultSet rs = kn.layDanhSachNhanVienChoTruongPhong();
            
            StringBuilder json = new StringBuilder();
            json.append("[");
            boolean first = true;
            
            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }
                json.append("{");
                json.append("\"id\":").append(rs.getInt("id")).append(",");
                json.append("\"ho_ten\":\"").append(rs.getString("ho_ten")).append("\",");
                json.append("\"chuc_vu\":\"").append(rs.getString("chuc_vu")).append("\"");
                json.append("}");
                first = false;
            }
            
            json.append("]");
            
            response.getWriter().write(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Lấy danh sách phòng ban khác (để chuyển nhân viên khi xóa)
     */
    private void layPhongBanKhac(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String phongBanId = request.getParameter("phongBanId");

        try (PrintWriter out = response.getWriter()) {
            KNCSDL kn = new KNCSDL();
            List<Map<String, Object>> danhSach = kn.layDanhSachPhongBanKhac(Integer.parseInt(phongBanId));

            out.print("[");
            for (int i = 0; i < danhSach.size(); i++) {
                Map<String, Object> pb = danhSach.get(i);
                out.print("{");
                out.print("\"id\":" + pb.get("id") + ",");
                out.print("\"ten_phong\":\"" + pb.get("ten_phong") + "\"");
                out.print("}");
                if (i < danhSach.size() - 1) {
                    out.print(",");
                }
            }
            out.print("]");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Lấy chi tiết phòng ban (AJAX call)
     */
    private void layChiTietPhongBan(HttpServletRequest request, HttpServletResponse response) 
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
