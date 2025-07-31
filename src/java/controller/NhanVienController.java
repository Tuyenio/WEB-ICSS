package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.*;

/**
 * Controller chính xử lý tất cả các chức năng CRUD cho Nhân viên
 * Gộp các servlet: dsnhanvien, themNhanvien, XoaNhanVien, locNhanvien
 */
public class NhanVienController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            // Mặc định: hiển thị danh sách nhân viên
            hienThiDanhSachNhanVien(request, response);
        } else {
            switch (action) {
                case "search":
                    timKiemNhanVien(request, response);
                    break;
                default:
                    hienThiDanhSachNhanVien(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            // Kiểm tra xem có phải là edit không (có empId)
            String empId = request.getParameter("empId");
            if (empId != null && !empId.trim().isEmpty()) {
                capNhatNhanVien(request, response);
            } else {
                themNhanVien(request, response);
            }
        } else {
            switch (action) {
                case "add":
                    themNhanVien(request, response);
                    break;
                case "edit":
                    capNhatNhanVien(request, response);
                    break;
                case "delete":
                    xoaNhanVien(request, response);
                    break;
                case "search":
                    timKiemNhanVien(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ");
            }
        }
    }

    /**
     * Hiển thị danh sách nhân viên
     */
    private void hienThiDanhSachNhanVien(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Map<String, Object>> danhSach = new ArrayList<>();

        try {
            KNCSDL kn = new KNCSDL();
            ResultSet rs = kn.laydl();

            while (rs.next()) {
                Map<String, Object> nv = new HashMap<>();
                nv.put("id", rs.getString("id"));
                nv.put("ho_ten", rs.getString("ho_ten"));
                nv.put("email", rs.getString("email"));
                nv.put("mat_khau", rs.getString("mat_khau"));
                nv.put("so_dien_thoai", rs.getString("so_dien_thoai"));
                nv.put("gioi_tinh", rs.getString("gioi_tinh"));
                nv.put("ngay_sinh", rs.getString("ngay_sinh"));
                nv.put("ten_phong_ban", rs.getString("ten_phong_ban"));
                nv.put("chuc_vu", rs.getString("chuc_vu"));
                nv.put("ngay_vao_lam", rs.getString("ngay_vao_lam"));
                nv.put("trang_thai_lam_viec", rs.getString("trang_thai_lam_viec"));
                nv.put("vai_tro", rs.getString("vai_tro"));
                danhSach.add(nv);
            }

            rs.close();

            // Gửi dữ liệu sang JSP
            request.setAttribute("danhSach", danhSach);
            request.getRequestDispatcher("employee.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h3 style='color:red'>❌ Lỗi: " + e.getMessage() + "</h3>");
        }
    }

    /**
     * Thêm nhân viên mới
     */
    private void themNhanVien(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        PrintWriter out = response.getWriter();

        try {
            String hoTen = request.getParameter("ho_ten");
            String email = request.getParameter("email");
            String matKhau = request.getParameter("mat_khau");
            String sdt = request.getParameter("so_dien_thoai");
            String gioiTinh = request.getParameter("gioi_tinh");
            String ngaySinh = request.getParameter("ngay_sinh");
            String ngayVaoLam = request.getParameter("ngay_vao_lam");
            String tenPhongBan = request.getParameter("ten_phong_ban");
            String chucVu = request.getParameter("chuc_vu");
            String trangThai = request.getParameter("trang_thai_lam_viec");
            String vaiTro = request.getParameter("vai_tro");

            KNCSDL kn = new KNCSDL();
            boolean result = kn.themNhanVien(hoTen, email, matKhau, sdt, gioiTinh,
                    ngaySinh, ngayVaoLam, tenPhongBan, chucVu, trangThai, vaiTro, "null");

            out.print(result ? "ok" : "error");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("error");
        }
    }

    /**
     * Cập nhật nhân viên
     */
    private void capNhatNhanVien(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String id = request.getParameter("empId");
        String hoTen = request.getParameter("ho_ten");
        String email = request.getParameter("email");
        String matKhau = request.getParameter("mat_khau");
        String sdt = request.getParameter("so_dien_thoai");
        String gioiTinh = request.getParameter("gioi_tinh");
        String ngaySinh = request.getParameter("ngay_sinh");
        String ngayVaoLam = request.getParameter("ngay_vao_lam");
        String tenPhongBan = request.getParameter("ten_phong_ban");
        String chucVu = request.getParameter("chuc_vu");
        String trangThai = request.getParameter("trang_thai_lam_viec");
        String vaiTro = request.getParameter("vai_tro");

        try {
            KNCSDL kn = new KNCSDL();
            boolean success = kn.capNhatNhanVien(Integer.parseInt(id), hoTen, email, matKhau, sdt, gioiTinh,
                    ngaySinh, ngayVaoLam, tenPhongBan, chucVu, trangThai, vaiTro, "null");

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
     * Xóa nhân viên
     */
    private void xoaNhanVien(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String id = request.getParameter("id");

        try {
            KNCSDL kn = new KNCSDL();
            boolean success = kn.xoaNhanVien(Integer.parseInt(id));

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
     * Tìm kiếm nhân viên
     */
    private void timKiemNhanVien(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String keyword = request.getParameter("keyword");
        String phongBan = request.getParameter("phong_ban");
        String trangThai = request.getParameter("trang_thai");
        String vaiTro = request.getParameter("vai_tro");

        try (PrintWriter out = response.getWriter()) {
            KNCSDL kn = new KNCSDL();
            List<Map<String, Object>> danhSach = kn.locNhanVien(keyword, phongBan, trangThai, vaiTro);

            if (danhSach != null && !danhSach.isEmpty()) {
                for (Map<String, Object> nv : danhSach) {
                    out.println("<tr>");
                    out.println("<td>" + nv.get("id") + "</td>");
                    out.println("<td><img src='https://i.pravatar.cc/40?img=1' class='rounded-circle' width='36'></td>");
                    out.println("<td><a href='#' class='emp-detail-link fw-semibold text-primary'>" + nv.get("ho_ten") + "</a></td>");
                    out.println("<td>" + nv.get("email") + "</td>");
                    out.println("<td>" + nv.get("so_dien_thoai") + "</td>");
                    out.println("<td>" + nv.get("gioi_tinh") + "</td>");
                    out.println("<td>" + nv.get("ngay_sinh") + "</td>");
                    out.println("<td>" + nv.get("ten_phong_ban") + "</td>");
                    out.println("<td>" + nv.get("chuc_vu") + "</td>");
                    out.println("<td>" + nv.get("ngay_vao_lam") + "</td>");
                    out.println("<td><span class='badge bg-success'>" + nv.get("trang_thai_lam_viec") + "</span></td>");
                    out.println("<td><span class='badge bg-info text-dark'>" + nv.get("vai_tro") + "</span></td>");
                    out.println("<td class='action-btns'>");
                    out.println("<button class='btn btn-sm btn-warning edit-emp-btn' data-id='" + nv.get("id") + "'"
                            + " data-name='" + nv.get("ho_ten") + "'"
                            + " data-email='" + nv.get("email") + "'"
                            + " data-pass='" + nv.get("mat_khau") + "'"
                            + " data-phone='" + nv.get("so_dien_thoai") + "'"
                            + " data-gender='" + nv.get("gioi_tinh") + "'"
                            + " data-birth='" + nv.get("ngay_sinh") + "'"
                            + " data-joindate='" + nv.get("ngay_vao_lam") + "'"
                            + " data-dept='" + nv.get("ten_phong_ban") + "'"
                            + " data-pos='" + nv.get("chuc_vu") + "'"
                            + " data-status='" + nv.get("trang_thai_lam_viec") + "'"
                            + " data-role='" + nv.get("vai_tro") + "'>");
                    out.println("<i class='fa-solid fa-pen'></i></button>");
                    out.println("<button class='btn btn-sm btn-danger delete-emp-btn' data-id='" + nv.get("id") + "'>");
                    out.println("<i class='fa-solid fa-trash'></i></button>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            } else {
                out.println("<tr><td colspan='13' class='text-center'>Không có dữ liệu phù hợp</td></tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='13' class='text-center text-danger'>Lỗi: " + e.getMessage() + "</td></tr>");
        }
    }
}
