<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>

<%
    List<Map<String, Object>> danhSach = (List<Map<String, Object>>) request.getAttribute("danhSach");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Phòng ban</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        body { background: #f4f6fa; }
        .sidebar {
            min-height: 100vh;
            background: linear-gradient(180deg, #23272f 0%, #343a40 100%);
            color: #fff;
            width: 240px;
            transition: width 0.2s;
            box-shadow: 2px 0 8px #0001;
            z-index: 10;
            position: fixed;
            top: 0; left: 0; bottom: 0;
        }
        .sidebar .sidebar-title {
            font-size: 1.7rem;
            font-weight: bold;
            letter-spacing: 1px;
            color: #0dcaf0;
            background: #23272f;
        }
        .sidebar-nav {
            padding: 0;
            margin: 0;
            list-style: none;
        }
        .sidebar-nav li {
            margin-bottom: 2px;
        }
        .sidebar-nav a {
            color: #fff;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 14px;
            padding: 14px 28px;
            border-radius: 8px;
            font-size: 1.08rem;
            font-weight: 500;
            transition: background 0.15s, color 0.15s;
        }
        .sidebar-nav a.active, .sidebar-nav a:hover {
            background: #0dcaf0;
            color: #23272f;
        }
        .sidebar-nav a .fa-solid {
            width: 26px;
            text-align: center;
            font-size: 1.25rem;
        }
        @media (max-width: 992px) {
            .sidebar { width: 60px; }
            .sidebar .sidebar-title { font-size: 1.1rem; padding: 12px 0; }
            .sidebar-nav a span { display: none; }
            .sidebar-nav a { justify-content: center; padding: 14px 0; }
        }
        .header {
            background: #fff;
            border-bottom: 1px solid #dee2e6;
            min-height: 64px;
            box-shadow: 0 2px 8px #0001;
            margin-left: 240px;
            position: sticky;
            top: 0;
            z-index: 20;
        }
        .avatar { width: 38px; height: 38px; border-radius: 50%; object-fit: cover; }
        .main-content { padding: 36px 36px 24px 36px; min-height: 100vh; margin-left: 240px; }
        .main-box { background: #fff; border-radius: 14px; box-shadow: 0 2px 12px #0001; padding: 32px 24px; }
        .table thead th { vertical-align: middle; }
        .table-hover tbody tr:hover { background: #eaf6ff; }
        .modal-content { border-radius: 14px; }
        .modal-header, .modal-footer { border-color: #e9ecef; }
        @media (max-width: 768px) {
            .main-box { padding: 10px 2px; }
            .main-content { padding: 10px 2px; margin-left: 60px; }
            .table-responsive { font-size: 0.95rem; }
            .header { margin-left: 60px; }
        }
        .search-container {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 24px;
        }
        .action-btns .btn {
            margin-right: 5px;
        }
    </style>
    <script>
        var PAGE_TITLE = '<i class="fa-solid fa-building me-2"></i>Quản lý Phòng ban';
    </script>
</head>
<body>
<div class="d-flex">
    <!-- Sidebar -->
    <nav class="sidebar p-0">
        <div class="sidebar-title text-center py-4 border-bottom border-secondary" style="cursor:pointer;" onclick="location.href='index.jsp'">
            <i class="fa-solid fa-people-group me-2"></i>ICSS
        </div>
        <ul class="sidebar-nav mt-3">
            <li>
                <a href="index.jsp"><i class="fa-solid fa-chart-line"></i><span>Dashboard</span></a>
            </li>
            <li>
                <a href="./dsnhanvien"><i class="fa-solid fa-users"></i><span>Nhân sự</span></a>
            </li>
            <li>
                <a href="task.jsp"><i class="fa-solid fa-tasks"></i><span>Công việc</span></a>
            </li>
            <li>
                <a href="./dsphongban" class="active"><i class="fa-solid fa-building"></i><span>Phòng ban</span></a>
            </li>
            <li>
                <a href="attendance.jsp"><i class="fa-solid fa-calendar-check"></i><span>Chấm công</span></a>
            </li>
            <li>
                <a href="report.jsp"><i class="fa-solid fa-chart-bar"></i><span>Báo cáo</span></a>
            </li>
        </ul>
    </nav>
    <!-- Main -->
    <div class="flex-grow-1">
        <!-- Header -->
        <%@ include file="header.jsp" %>
        <div class="main-content">

            <div class="main-box">
                <!-- Tiêu đề và nút thêm -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h4 class="mb-0">
                        <i class="fa-solid fa-building me-2 text-primary"></i>
                        Danh sách Phòng ban
                    </h4>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalDepartment">
                        <i class="fa-solid fa-plus me-2"></i>Thêm phòng ban
                    </button>
                </div>

                <!-- Tìm kiếm -->
                <div class="search-container">
                    <div class="row">
                        <div class="col-md-8">
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="fa-solid fa-search"></i>
                                </span>
                                <input type="text" class="form-control" id="searchKeyword"
                                    placeholder="Tìm kiếm theo tên phòng ban hoặc trưởng phòng...">
                            </div>
                        </div>
                        <div class="col-md-4">
                            <button type="button" class="btn btn-outline-primary me-2" id="btnSearch">
                                <i class="fa-solid fa-search me-1"></i>Tìm kiếm
                            </button>
                            <button type="button" class="btn btn-outline-secondary" id="btnReset">
                                <i class="fa-solid fa-refresh me-1"></i>Làm mới
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Bảng dữ liệu -->
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th style="width: 60px;">ID</th>
                                <th>Tên phòng ban</th>
                                <th>Trưởng phòng</th>
                                <th style="width: 120px;">Số nhân viên</th>
                                <th style="width: 140px;">Ngày tạo</th>
                                <th style="width: 140px;">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="deptTableBody">
                            <% if (danhSach != null && !danhSach.isEmpty()) { %>
                                <% for (Map<String, Object> pb : danhSach) { %>
                                    <tr>
                                        <td><%= pb.get("id") %></td>
                                        <td class="fw-semibold"><%= pb.get("ten_phong") %></td>
                                        <td>
                                            <%= pb.get("ten_truong_phong") != null ? pb.get("ten_truong_phong") : "<span class='text-muted'>Chưa có</span>" %>
                                        </td>
                                        <td><span class="badge bg-info"><%= pb.get("so_nhan_vien") %> người</span></td>
                                        <td><%= pb.get("ngay_tao") %></td>
                                        <td class="action-btns">
                                            <button class="btn btn-sm btn-primary detail-dept-btn"
                                                data-id="<%= pb.get("id") %>">
                                                <i class="fa-solid fa-eye"></i>
                                            </button>
                                            <button class="btn btn-sm btn-warning edit-dept-btn"
                                                data-id="<%= pb.get("id") %>"
                                                data-name="<%= pb.get("ten_phong") %>"
                                                data-manager="<%= pb.get("truong_phong_id") != null ? pb.get("truong_phong_id") : "" %>">
                                                <i class="fa-solid fa-pen"></i>
                                            </button>
                                            <button class="btn btn-sm btn-danger delete-dept-btn"
                                                data-id="<%= pb.get("id") %>">
                                                <i class="fa-solid fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="6" class="text-center">Không có dữ liệu</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Modal Thêm/Sửa phòng ban -->
            <div class="modal fade" id="modalDepartment" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modalDepartmentTitle">
                                <i class="fa-solid fa-building me-2"></i>Thêm phòng ban
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <form id="departmentForm">
                            <div class="modal-body">
                                <input type="hidden" id="deptId" name="deptId">
                                
                                <div class="mb-3">
                                    <label for="ten_phong" class="form-label">Tên phòng ban <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="ten_phong" name="ten_phong" required>
                                </div>

                                <div class="mb-3">
                                    <label for="truong_phong_id" class="form-label">Trưởng phòng</label>
                                    <select class="form-select" id="truong_phong_id" name="truong_phong_id">
                                        <option value="">-- Chọn trưởng phòng --</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                                <button type="submit" class="btn btn-primary">Lưu</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Modal chi tiết phòng ban -->
            <div class="modal fade" id="modalDeptDetail" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">
                                <i class="fa-solid fa-info-circle me-2"></i>Chi tiết phòng ban
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div id="deptDetailContent">
                                <!-- Nội dung sẽ được load bằng AJAX -->
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Modal chuyển nhân viên khi xóa phòng ban -->
            <div class="modal fade" id="modalTransferEmployee" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header bg-warning">
                            <h5 class="modal-title">
                                <i class="fa-solid fa-exclamation-triangle me-2"></i>Chuyển nhân viên
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="alert alert-warning">
                                <i class="fa-solid fa-info-circle me-2"></i>
                                Phòng ban này còn có nhân viên. Bạn cần chuyển họ sang phòng ban khác trước khi xóa.
                            </div>
                            <form id="transferForm">
                                <input type="hidden" id="phongBanCanXoa" name="phongBanCanXoa">
                                <div class="mb-3">
                                    <label for="phongBanMoi" class="form-label">Chuyển sang phòng ban</label>
                                    <select class="form-select" id="phongBanMoi" name="phongBanMoi" required>
                                        <option value="">-- Chọn phòng ban --</option>
                                    </select>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="button" class="btn btn-danger" id="btnConfirmTransferDelete">
                                <i class="fa-solid fa-trash me-2"></i>Chuyển và xóa
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- Toast -->
<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 9999">
    <div id="toastSuccess" class="toast align-items-center text-bg-success border-0" role="alert">
        <div class="d-flex">
            <div class="toast-body">
                Thao tác thành công!
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    </div>
    <div id="toastError" class="toast align-items-center text-bg-danger border-0" role="alert">
        <div class="d-flex">
            <div class="toast-body">
                Đã xảy ra lỗi!
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    </div>
</div>
<script>
    $(document).ready(function() {
        // Load danh sách nhân viên cho dropdown
        loadEmployeesForDropdown();

        // Tìm kiếm
        $('#btnSearch').click(function() {
            performSearch();
        });

        $('#searchKeyword').keypress(function(e) {
            if (e.which == 13) {
                performSearch();
            }
        });

        // Làm mới
        $('#btnReset').click(function() {
            $('#searchKeyword').val('');
            location.reload();
        });

        // Nút thêm phòng ban
        $('.btn[data-bs-target="#modalDepartment"]').click(function() {
            resetForm();
            $('#modalDepartmentTitle').html('<i class="fa-solid fa-building me-2"></i>Thêm phòng ban');
        });

        // Nút sửa
        $(document).on('click', '.edit-dept-btn', function() {
            var id = $(this).data('id');
            var name = $(this).data('name');
            var managerId = $(this).data('manager');

            $('#deptId').val(id);
            $('#ten_phong').val(name);
            $('#truong_phong_id').val(managerId);
            $('#modalDepartmentTitle').html('<i class="fa-solid fa-pen me-2"></i>Sửa phòng ban');
            $('#modalDepartment').modal('show');
        });

        // Nút xoá
        $(document).on('click', '.delete-dept-btn', function() {
            var id = $(this).data('id');
            Swal.fire({
                title: 'Xác nhận xoá?',
                text: 'Bạn có chắc chắn muốn xoá phòng ban này?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Xoá',
                cancelButtonText: 'Huỷ'
            }).then((result) => {
                if (result.isConfirmed) {
                    deleteDepartment(id);
                }
            });
        });

        // Nút chi tiết
        $(document).on('click', '.detail-dept-btn', function() {
            var id = $(this).data('id');
            loadDepartmentDetail(id);
        });

        // Xử lý nút xác nhận chuyển và xóa
        $('#btnConfirmTransferDelete').click(function() {
            var phongBanCanXoa = $('#phongBanCanXoa').val();
            var phongBanMoi = $('#phongBanMoi').val();
            
            if (!phongBanMoi) {
                showToast('error', 'Vui lòng chọn phòng ban!');
                return;
            }
            
            deleteDepartmentWithTransfer(phongBanCanXoa, phongBanMoi);
        });

        // Submit form thêm/sửa
        $('#departmentForm').on('submit', function(e) {
            e.preventDefault();
            
            var formData = $(this).serialize();
            var isEdit = $('#deptId').val() !== '';
            var url = isEdit ? './dsphongban' : './themPhongBan';

            $.ajax({
                url: url,
                type: 'POST',
                data: formData,
                success: function(response) {
                    var success = false;
                    
                    // Kiểm tra response dạng text hoặc JSON
                    if (typeof response === 'string') {
                        var trimmed = response.trim();
                        if (trimmed === 'ok' || trimmed === '"ok"') {
                            success = true;
                        } else {
                            try {
                                var jsonResponse = JSON.parse(trimmed);
                                if (jsonResponse.status === 'ok') {
                                    success = true;
                                }
                            } catch(e) {
                                // Không phải JSON, giữ success = false
                            }
                        }
                    } else if (response && response.status === 'ok') {
                        success = true;
                    }
                    
                    if (success) {
                        $('#modalDepartment').modal('hide');
                        showToast('success', isEdit ? 'Cập nhật thành công!' : 'Thêm mới thành công!');
                        setTimeout(() => location.reload(), 1000);
                    } else {
                        showToast('error', 'Có lỗi xảy ra!');
                    }
                },
                error: function() {
                    showToast('error', 'Có lỗi xảy ra!');
                }
            });
        });

        // Toast init
        $('.toast').toast({ delay: 3000 });
    });

    // Hàm load danh sách nhân viên cho dropdown
    function loadEmployeesForDropdown() {
        $.get('./layNhanVienChoTruongPhong?action=employees', function(data) {
            var options = '<option value="">-- Chọn trưởng phòng --</option>';
            data.forEach(function(emp) {
                options += '<option value="' + emp.id + '">' + emp.ho_ten + ' (' + emp.chuc_vu + ')</option>';
            });
            $('#truong_phong_id').html(options);
        }).fail(function() {
            console.log('Không thể load danh sách nhân viên');
        });
    }

    // Hàm tìm kiếm
    function performSearch() {
        var keyword = $('#searchKeyword').val();
        
        $.ajax({
            url: './locPhongBan?action=search',
            type: 'POST',
            data: { keyword: keyword },
            success: function(response) {
                $('#deptTableBody').html(response);
            },
            error: function() {
                showToast('error', 'Lỗi tìm kiếm!');
            }
        });
    }

    // Hàm xóa phòng ban
    function deleteDepartment(id) {
        $.ajax({
            url: './xoaPhongBan?action=delete',
            type: 'POST',
            data: { id: id },
            dataType: 'json',
            success: function(response) {
                if (response.status === 'ok') {
                    showToast('success', 'Xoá thành công!');
                    setTimeout(() => location.reload(), 1000);
                } else if (response.status === 'need_transfer') {
                    // Hiện modal chuyển nhân viên
                    $('#phongBanCanXoa').val(id);
                    loadOtherDepartments(id);
                    $('#modalTransferEmployee').modal('show');
                } else {
                    showToast('error', response.message || 'Có lỗi xảy ra khi xoá!');
                }
            },
            error: function(xhr) {
                try {
                    var response = JSON.parse(xhr.responseText);
                    showToast('error', response.message || 'Có lỗi xảy ra!');
                } catch(e) {
                    showToast('error', 'Có lỗi xảy ra!');
                }
            }
        });
    }

    // Hàm xóa phòng ban với chuyển nhân viên
    function deleteDepartmentWithTransfer(phongBanCanXoa, phongBanMoi) {
        $.ajax({
            url: './xoaPhongBan?action=delete',
            type: 'POST',
            data: { 
                id: phongBanCanXoa,
                phongBanMoi: phongBanMoi
            },
            dataType: 'json',
            success: function(response) {
                if (response.status === 'ok') {
                    $('#modalTransferEmployee').modal('hide');
                    showToast('success', 'Đã chuyển nhân viên và xoá phòng ban thành công!');
                    setTimeout(() => location.reload(), 1000);
                } else {
                    showToast('error', response.message || 'Có lỗi xảy ra!');
                }
            },
            error: function(xhr) {
                try {
                    var response = JSON.parse(xhr.responseText);
                    showToast('error', response.message || 'Có lỗi xảy ra!');
                } catch(e) {
                    showToast('error', 'Có lỗi xảy ra!');
                }
            }
        });
    }

    // Hàm load danh sách phòng ban khác
    function loadOtherDepartments(phongBanId) {
        $.get('./layPhongBanKhac?action=other-departments&phongBanId=' + phongBanId, function(data) {
            var options = '<option value="">-- Chọn phòng ban --</option>';
            data.forEach(function(dept) {
                options += '<option value="' + dept.id + '">' + dept.ten_phong + '</option>';
            });
            $('#phongBanMoi').html(options);
        }).fail(function() {
            showToast('error', 'Không thể load danh sách phòng ban!');
        });
    }

    // Hàm load chi tiết phòng ban
    function loadDepartmentDetail(id) {
        $('#deptDetailContent').html('<div class="text-center"><i class="fa-solid fa-spinner fa-spin"></i> Đang tải...</div>');
        $('#modalDeptDetail').modal('show');
        
        $.get('./chiTietPhongBan?action=detail&id=' + id, function(data) {
            $('#deptDetailContent').html(data);
        }).fail(function() {
            $('#deptDetailContent').html('<div class="alert alert-danger">Không thể tải thông tin chi tiết!</div>');
        });
    }

    // Hàm reset form
    function resetForm() {
        $('#departmentForm')[0].reset();
        $('#deptId').val('');
    }

    // Hàm hiện toast
    function showToast(type, message) {
        if (type === 'success') {
            $('#toastSuccess .toast-body').text(message);
            $('#toastSuccess').toast('show');
        } else {
            $('#toastError .toast-body').text(message);
            $('#toastError').toast('show');
        }
    }
</script>
</body>
</html>
