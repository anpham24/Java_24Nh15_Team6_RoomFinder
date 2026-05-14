1. LoginFrame (Màn hình Đăng nhập):
Hoạt động: Đây là màn hình đầu tiên khi chạy app. Người dùng nhập txtUsername và txtPassword.
Sự kiện: Khi bấm btnLogin, hệ thống truy vấn Database (bảng users và accounts). Tùy thuộc vào cột role trả về (TENANT, LANDLORD, ADMIN), hệ thống sẽ đóng cửa sổ này và mở ra MainFrame tương ứng.
Chuyển hướng: Nếu chưa có tài khoản, bấm btnNewAccount sẽ mở RegisterFrame.

2. RegisterFrame (Màn hình Đăng ký):
Hoạt động: Người dùng điền thông tin và chọn vai trò thông qua 2 Radio Button (rdoTenant hoặc rdoLandlord). Lưu ý: Không có nút chọn Admin ở đây, Admin phải được thêm tay vào Database.
Sự kiện: Bấm btnRegister -> Insert dữ liệu vào Database -> Báo thành công và quay ngược lại LoginFrame.

3. TenantMainFrame: 
3.1. Thanh Header (jPanel1):
Chứa tiêu đề ứng dụng "Hệ thống tìm kiếm phòng trọ" (jLabel1).
Có nút Đăng xuất (btnLogout) ở góc phải để người dùng quay lại LoginFrame.
3.2. Thanh Tìm kiếm cơ bản:
Gồm ô nhập từ khóa txtSearch và nút btnSearch (Tìm kiếm). Dùng để tìm nhanh theo tên đường, quận/huyện hoặc tiêu đề phòng.
3.3. Khu vực Bộ lọc & Sắp xếp (pnSort): Đây là phần "ăn tiền" nhất của hệ thống tìm kiếm:
Sắp xếp (btgSort): Nhóm 2 Radio Button là rdoPrice (Giá tiền) và rdoReview (Đánh giá). Tính năng này giúp người thuê ưu tiên xem phòng rẻ nhất hoặc phòng uy tín nhất trước.
Khoảng giá: Gồm 2 ô nhập liệu txtMinPrice và txtMaxPrice.
Tiện nghi (pnAmenity): Panel này đang được set GridLayout(0, 3) (Không giới hạn hàng, chia 3 cột). Chỗ này được sinh ra để bạn dùng code chèn các ô Checkbox (VD: [x] Wifi, [x] Chỗ để xe, [x] Nuôi thú cưng) vào.
Nút Áp dụng (btnApply): Nhấn nút này thì hệ thống mới gom hết các điều kiện lọc (Giá, Tiện nghi, Sắp xếp) để truy vấn Database.
3.4. Khu vực Hiển thị danh sách phòng (jScrollPane1 & pnRoomList):
pnRoomList là trái tim của màn hình này. Nó được set Layout là GridLayout(0, 3, 20, 20) (Không giới hạn số hàng, luôn luôn có 3 cột, khoảng cách các cột/hàng là 20px). Thiết kế này hoàn toàn khớp với kích thước của RoomCardPanel 
💡 Hướng dẫn Code Logic (Controller) cho Backend
Khi nhóm bạn làm logic cho Form này, hãy chú ý 3 bước sau:
Bước 1: Khởi tạo động Checkbox tiện nghi lúc mới mở Form Hệ thống phải gọi Database lấy bảng amenities, lặp qua danh sách và add các JCheckBox vào pnAmenity.
Bước 2: Hiển thị danh sách phòng (Phép thuật nằm ở đây) Khi gọi DB bảng rooms (kèm điều kiện status = APPROVED), với mỗi kết quả trả về, Backend phải làm đúng các thao tác sau:
// Giả mã (Pseudocode) cho team Backend:
pnRoomList.removeAll(); // Xóa sạch dữ liệu cũ

for (RoomDTO room : danhSachPhong) {
    RoomCardPanel card = new RoomCardPanel();
    
    // 1. Đổ dữ liệu vào card
    card.getLbTitle().setText(room.getTitle());
    // ... đổ giá, diện tích, ảnh...
    
    // 2. Ép thẻ này biến thành "Chế độ người thuê" (Giấu các nút của Admin/Chủ trọ)
    // 3. Nhét thẻ vào danh sách
    pnRoomList.add(card);
}
// Yêu cầu Giao diện vẽ lại
pnRoomList.revalidate();
pnRoomList.repaint();

Bước 3: Xử lý chức năng Bộ lọc Khi người dùng bấm btnSearch hoặc btnApply, Backend cần lấy chữ trong txtSearch, kết hợp với biến boolean từ các Checkbox tiện nghi và radio button để build ra câu lệnh SQL như: SELECT * FROM rooms WHERE title LIKE '%...%' AND price >= min AND price <= max ORDER BY price ASC/DESC. Sau đó gọi lại hàm ở Bước 2.

4. RoomCardPanel (Thẻ tóm tắt phòng trọ)
Vai trò: Đây là một JPanel đóng vai trò như một "món hàng" được trưng bày trên trang chủ. Nó được sinh ra liên tục (bằng vòng lặp) dựa trên số lượng phòng lấy từ Database và được nhét vào màn hình chính của Người thuê hoặc Chủ trọ.
Cấu trúc Giao diện (Từ trên xuống dưới):
Ảnh đại diện: lbThumb - Một không gian cố định (cao 200px) để chứa ảnh thumbnail của phòng.
Tiêu đề & Trạng thái thuê: lbTitle (In đậm) nằm bên trái, lbAvailability (Còn/hết) nằm bên phải.
Tiện nghi: pnAmenity - Một Panel sử dụng GridLayout (1, 3). Chỗ này dùng để bạn code động chèn các Label tiện nghi (VD: Wifi, Điều hòa) vào.
Địa chỉ: lbAddress - Cắt ngắn địa chỉ nếu quá dài.
Chỉ số cơ bản: Cùng một hàng có lbPrice (Màu đỏ), lbArea (Màu xám) và lbRating (Điểm đánh giá trung bình).
Thanh công cụ quản lý (Dưới cùng): Cụm này chứa các nút btnAvailability (Đổi trạng thái còn/hết), btnUpdate (Sửa), btnDelete (Xóa) và lbStatus (Chờ duyệt/Đã duyệt).
Logic cần code ở Backend (Controller):
Chế độ Người thuê (Tenant): Controller gọi thẻ này phải dùng lệnh btnUpdate.setVisible(false), btnDelete.setVisible(false), jButton1.setVisible(false) và lbStatus.setVisible(false) để giấu sạch các nút quản lý đi.
Chế độ Chủ trọ (Landlord): Bật các nút quản lý lên. Code sự kiện cho btnAvailability để gạt cờ availability trong DB từ True sang False và ngược lại. Code sự kiện Click vào ảnh lbThumb để mở ra RoomDetailFrame.

5. RoomDetailFrame (Màn hình Chi tiết phòng)
Vai trò: Là một JFrame độc lập (kích thước khá lớn 1280x720) bật lên khi người dùng click vào một RoomCardPanel cụ thể. Nó chứa 100% thông tin của phòng đó, bao gồm cả phần Đánh giá (Review).
Cấu trúc Giao diện (Chia làm 2 nửa):
Nửa bên Trái (Khu vực Hình ảnh):
Ảnh chính: lbMainImage - Khung ảnh siêu to (550x560).
Danh sách ảnh con: Bên dưới có một thanh cuộn ngang jScrollPane1 chứa pnImageList.
Logic Backend: Bạn sẽ truy vấn bảng room_images lấy ra List các link ảnh. Dùng vòng lặp tạo các JLabel chứa ảnh nhỏ nhét vào pnImageList. Khi người dùng click vào ảnh nhỏ nào, đổi source của lbMainImage thành ảnh đó.
Nửa bên Phải (Khu vực Thông tin & Tương tác):
Thông tin Header: lbTitle, lbAvailability, lbRating, lbStatus.
Mô tả chi tiết: lbDescription - Khung Text hiển thị nội dung chủ trọ quảng cáo.
Tiện nghi: pnAmenity - Tương tự như ở Card nhưng có thể hiển thị đầy đủ tất cả tiện nghi của phòng.
Thông tin cơ bản: lbAddress, lbPrice, lbArea.
Thanh Liên hệ / Quản lý: * Thông tin chủ trọ: lbPhone (Số điện thoại để người thuê gọi).
Nút quản lý: btnAvailability (Đổi còn/hết), btnUpdate, btnDelete nằm ngay cạnh.
Khu vực Đánh giá (Review):
Lịch sử Review: jScrollPane2 chứa pnReviewList. Controller sẽ truy vấn bảng reviews của phòng này, với mỗi dòng dữ liệu sẽ khởi tạo một đối tượng ReviewPanel và .add() vào pnReviewList.
Giao diện đăng Review mới: Ở dưới cùng có ô nhập text txtReview, ComboBox chọn sao cboRating (1-5), và nút Đăng btnSubmit.
Logic "Biến hình" cần code ở Backend (Controller): Vì form này xài chung cho cả 3 Role, nên ngay trong constructor hoặc Controller của Form này, bạn phải truyền vào biến User currentUser.
Nếu currentUser là Người thuê: Hiện phần đăng Review (txtReview, btnSubmit), hiện lbPhone. Giấu các nút btnUpdate, btnDelete, btnAvailability, lbStatus.
Nếu currentUser là Chủ trọ (của chính phòng này): Hiện btnUpdate, btnDelete, btnAvailability, lbStatus. Đóng băng hoặc ẩn phần đăng Review (txtReview, btnSubmit setVisible = false) vì chủ trọ không được tự đánh giá phòng của mình.
Nếu currentUser là Admin: Giống hệt Chủ trọ, nhưng thay vì nút "Sửa", có thể chỉ có nút "Xóa bài" và không có chức năng đăng Review.

6. ReviewPanel (Thẻ hiển thị một đánh giá)
Vai trò: Khác với các Frame (cửa sổ lớn), ReviewPanel là một mảnh ghép (Sub-component). Bạn có thể tưởng tượng nó giống như một cái bình luận (comment) trên Facebook. Nếu một phòng trọ có 10 đánh giá, hệ thống sẽ sinh ra 10 cái ReviewPanel này và xếp chồng lên nhau nhét vào trong pnReviewList (nằm ở RoomDetailFrame).
Cấu trúc Giao diện:
lbName (Tên người đánh giá): Nằm ở trên cùng, được in đậm (Bold) để nổi bật tên của Người thuê (Tenant) đã viết đánh giá.
lbContent (Nội dung chi tiết): Nằm ngay dưới tên, chiếm phần lớn chiều rộng của Panel (636px) và được thiết lập chiều cao khá lớn (95px) để chứa được các câu bình luận dài.
lbRating (Số sao): Nằm bên phải của nội dung bình luận, hiển thị điểm đánh giá (từ 1 đến 5 sao).
Độ trong suốt: File có gọi lệnh setOpaque(false), nghĩa là nền của thẻ này hoàn toàn trong suốt. Nó sẽ tự động lấy màu nền của cái bảng pnReviewList chứa nó.
Logic cần code ở Backend (Controller):
Vòng lặp sinh Panel: Backend sẽ viết một vòng lặp for lướt qua danh sách các đánh giá lấy từ Database. Với mỗi đánh giá, code sẽ gọi ReviewPanel panel = new ReviewPanel();, sau đó dùng hàm setter đẩy dữ liệu vào panel.lbName.setText(tên), và cuối cùng dùng pnReviewList.add(panel) để nhét nó lên màn hình.
Mẹo ép xuống dòng cho lbContent: Vì lbContent là một JLabel, mặc định trong Java Swing, JLabel sẽ KHÔNG tự động xuống dòng nếu chữ quá dài (nó sẽ bị tràn ra khỏi màn hình hoặc bị cắt mất bằng dấu ...). Để fix lỗi này, Backend khi lấy data từ DB lên phải bọc nội dung bình luận vào thẻ HTML. Ví dụ: lbContent.setText("<html><body style='width: 450px'>" + noiDungBinhLuan + "</body></html>");.

7. LandlordMainFrame:
Màn hình quản lý các tài sản của Chủ trọ.
Tải dữ liệu: Mở lên, truy vấn các phòng thuộc sở hữu của User hiện tại (WHERE landlord_id = ?). Hiển thị lên pnRoomList dưới dạng các RoomCardPanel.
Thêm phòng mới:
Bấm btnAddRoom(Thêm phòng mới) -> Bật Dialog RoomActionDialog (Truyền tham số rỗng vào).
Chủ trọ điền thông tin, chọn ảnh (btnBrowse), tích chọn tiện nghi -> Bấm btnSave -> Insert vào DB với trạng thái mặc định là PENDING (Chờ duyệt). Đóng form và load lại danh sách.
Cập nhật/Xóa phòng:
Trên mỗi RoomCardPanel của chủ trọ sẽ có nút btnUpdate và btnDelete.
Bấm btnUpdate -> Bật RoomActionDialog lên nhưng lần này nhét sẵn dữ liệu cũ vào các ô Text để chủ trọ sửa. Bấm btnSave -> Update DB.
Bấm "Đổi còn/hết" (btnAvailability trên Card) -> Đổi nhanh trạng thái availability từ True sang False (Đã cho thuê).

8. RoomActionDialog:
Tiêu đề (Header):
jLabel1: "Thêm/sửa phòng".
Khu vực Nhập liệu dạng Text (Input Fields): Bạn đã thiết kế rất đầy đủ các trường thông tin cơ bản:
txtTitle: Nhập tiêu đề bài đăng.
txtDescription: Nhập mô tả.
txtAddress: Nhập địa chỉ.
txtPrice: Nhập giá tiền.
txtArea: Nhập diện tích.
Khu vực Động (Dynamic Areas):
pnAmenity (Bảng Tiện nghi): Đang dùng Layout GridLayout(0, 3, 20, 20). Rất chuẩn! Khi form mở lên, Backend sẽ truy vấn DB lấy ra danh sách tiện nghi và đẩy các JCheckBox vào đây.
pnImageList và btnBrowse: Nút btnBrowse dùng để mở JFileChooser chọn ảnh từ máy tính. Ảnh chọn xong sẽ được thu nhỏ và add vào pnImageList để xem trước.
Khu vực Nút bấm (Actions):
btnSave (Lưu) và btnExit (Hủy/Thoát).

9. AdminMainFrame:
Sử dụng JTabbedPane với 4 Tab xử lý dữ liệu qua dạng Bảng (JTable).
Tab Duyệt bài (pnApproveRoom): Load danh sách phòng có trạng thái PENDING. Admin chọn 1 dòng, bấm "Xem chi tiết" để bật RoomDetailFrame soi kỹ nội dung. Xong quay lại bấm btnApproveRoom (Đổi thành APPROVED) hoặc btnDeclineRoom.
Tab Quản lý bài đăng (pnRoomManage): Xem toàn bộ phòng trong hệ thống. Có thể dùng Combo box cboStatus để lọc. Admin có quyền ấn btnDeleteRoom để xóa bay màu bài viết vi phạm.
Tab Quản lý người dùng (pnUserManage): Hiển thị User. Dùng để "thanh trừng" (btnDeleteUser) các tài khoản lừa đảo.
Tab Quản lý tiện nghi (pnAmenityManage): Thêm, sửa, xóa các loại tiện nghi (như Wifi, Thang máy...) để nó xuất hiện dưới dạng Checkbox cho Chủ trọ chọn lúc đăng bài.


Mô tả chi tiết:
Phần Header (Tiêu đề trên cùng)
Hiển thị tên ứng dụng: "Hệ thống tìm kiếm phòng trọ" nằm ở góc trái.
Nút "Đăng xuất" (btnLogout) nằm ở góc phải để Admin thoát khỏi hệ thống.
Phần Nội dung chính (Các Tab Quản lý)
Phần trung tâm của giao diện sử dụng JTabbedPane chứa 4 thẻ (tab) chức năng chính:
Tab 1: "Duyệt bài" (pnApproveRoom)
Tab này dùng để xử lý các bài đăng phòng trọ mới được chủ trọ gửi lên hệ thống.
Bảng dữ liệu (tbApproveRoom): Hiển thị danh sách các bài đang chờ duyệt với các cột: ID, Tiêu đề, Chủ trọ, Ngày đăng, Trạng thái. Bảng này chỉ cho phép xem, không cho phép chỉnh sửa trực tiếp.
Các nút chức năng (nằm ở góc phải bên dưới):
"Xem chi tiết" (btnRoomDetail_tab1): Mở chi tiết bài đăng để kiểm tra. Mở RoomDetailFrame (chế độ Admin)
"Duyệt" (btnApproveRoom): Chấp thuận bài đăng lên hệ thống.
"Từ chối" (btnDeclineRoom): Hủy hoặc từ chối bài đăng.
Tab 2: "Quản lý bài đăng" (pnRoomManage)
Tab này dùng để quản lý toàn bộ các bài đăng đang tồn tại trên hệ thống.
Thanh công cụ tìm kiếm & lọc:
Ô nhập chữ "Tìm kiếm" (txtSearchRoom).
Bộ lọc "Trạng thái" (cboStatus): Cho phép lọc theo Tất cả, Chờ duyệt, Đã duyệt, Bị từ chối.
Nút "Tìm/Lọc" (btnSearchRoom) để thực thi.
Bảng dữ liệu (tbRoomManage): Hiển thị danh sách bài đăng với các cột: ID, Tiêu đề, Chủ trọ, Giá thuê, Trạng thái.
Các nút chức năng:
"Xem chi tiết" (btnRoomDetail_tab2). Mở RoomDetailFrame (chế độ Admin)
"Xóa" (btnDeleteRoom): Xóa bài đăng khỏi hệ thống.
Tab 3: "Quản lý người dùng" (pnUserManage)
Cho phép quản trị viên giám sát và thao tác với các tài khoản người dùng.
Thanh công cụ tìm kiếm & lọc:
Ô nhập chữ "Tìm kiếm" (txtSearchUser).
Bộ lọc "Vai trò" (cboRole): Lọc người dùng theo vai trò là Chủ trọ hoặc Người thuê.
Nút "Tìm/Lọc" (btnSearchUser).
Bảng dữ liệu (tbUserManage): Liệt kê thông tin người dùng với các cột: ID, Tên, Số điện thoại, Vai trò, Trạng thái.
Nút chức năng: Chỉ có duy nhất một nút "Xóa" (btnDeleteUser) ở bên dưới để xóa tài khoản người dùng vi phạm.
Tab 4: "Quản lý tiện nghi" (pnAmenityManage)
Dùng để thiết lập các tiện ích có thể có trong phòng trọ (ví dụ: máy lạnh, máy giặt, wifi...).
Bảng dữ liệu (tbAmenityManage): Đơn giản chỉ gồm 2 cột: ID, Tên tiện nghi.
Các nút chức năng:
"Thêm" (btnAddAmenity): Thêm tiện nghi mới vào cơ sở dữ liệu. Tạo Dialog để Admin thêm
"Sửa" (btnUpdateAmenity): Chỉnh sửa tên tiện nghi hiện tại. Tạo dialog để admin sửa
"Xóa" (btnDeleteAmenity): Xóa tiện nghi khỏi danh sách.


