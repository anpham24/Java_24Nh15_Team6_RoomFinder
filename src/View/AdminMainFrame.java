/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

/**
 *
 * @author anpha
 */
public class AdminMainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminMainFrame.class.getName());

    private DTO.UserDTO currentUser;

    private final BLL.AdminController adminBLL = new BLL.AdminController();

    public AdminMainFrame(DTO.UserDTO user) {
        this.currentUser = user;
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        setupTableModels();
        initEvents();
        loadApproveTab();
        loadRoomManageTab(null, null);
        loadUserManageTab(null, null);
        loadAmenityTab();
    }

    public AdminMainFrame() { this(null); }

    public DTO.UserDTO getCurrentUser() { return currentUser; }

    private void setupTableModels() {
        tbApproveRoom.setModel(new javax.swing.table.DefaultTableModel(
            new String[]{"ID", "Tiêu đề", "Chủ trọ", "Ngày đăng", "Trạng thái"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        tbRoomManage.setModel(new javax.swing.table.DefaultTableModel(
            new String[]{"ID", "Tiêu đề", "Chủ trọ", "Giá thuê", "Trạng thái"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        tbUserManage.setModel(new javax.swing.table.DefaultTableModel(
            new String[]{"ID", "Tên", "Số điện thoại", "Vai trò"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        tbAmenityManage.setModel(new javax.swing.table.DefaultTableModel(
            new String[]{"ID", "Tên tiện nghi"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void initEvents() {
        btnLogout.addActionListener(e -> {
            int c = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?", "Đăng xuất",
                    javax.swing.JOptionPane.YES_NO_OPTION);
            if (c == javax.swing.JOptionPane.YES_OPTION) { dispose(); new TenantMainFrame().setVisible(true); }
        });
        btnRoomDetail_tab1.addActionListener(e -> openRoomDetail(tbApproveRoom));
        btnApproveRoom.addActionListener(e -> handleApprove(true));
        btnDeclineRoom.addActionListener(e -> handleApprove(false));
        btnSearchRoom.addActionListener(e -> handleSearchRoom());
        btnRoomDetail_tab2.addActionListener(e -> openRoomDetail(tbRoomManage));
        btnDeleteRoom.addActionListener(e -> handleDeleteRoom());
        btnSearchUser.addActionListener(e -> handleSearchUser());
        btnDeleteUser.addActionListener(e -> handleDeleteUser());
        btnAddAmenity.addActionListener(e -> handleAddAmenity());
        btnUpdateAmenity.addActionListener(e -> handleUpdateAmenity());
        btnDeleteAmenity.addActionListener(e -> handleDeleteAmenity());
    }

    private void loadApproveTab() {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tbApproveRoom.getModel();
        m.setRowCount(0);
        for (DTO.RoomDTO r : adminBLL.getPendingRooms()) {
            DTO.UserDTO landlord = adminBLL.getUserById(r.getLandlordId());
            m.addRow(new Object[]{ r.getRoomId(), r.getTitle(),
                landlord != null ? landlord.getName() : r.getLandlordId(),
                r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : "",
                "Chờ duyệt" });
        }
    }

    private void loadRoomManageTab(String keyword, String statusFilter) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tbRoomManage.getModel();
        m.setRowCount(0);
        for (DTO.RoomDTO r : adminBLL.searchRooms(keyword, statusFilter)) {
            DTO.UserDTO landlord = adminBLL.getUserById(r.getLandlordId());
            m.addRow(new Object[]{ r.getRoomId(), r.getTitle(),
                landlord != null ? landlord.getName() : r.getLandlordId(),
                String.format("%,.0f", r.getPrice()), statusToLabel(r.getStatus()) });
        }
    }

    private void loadUserManageTab(String keyword, DTO.UserDTO.Role roleFilter) {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tbUserManage.getModel();
        m.setRowCount(0);
        for (DTO.UserDTO u : adminBLL.searchUsers(keyword, roleFilter))
            m.addRow(new Object[]{ u.getUserId(), u.getName(), u.getPhoneNumber(),
                u.getRole() == DTO.UserDTO.Role.LANDLORD ? "Chủ trọ" : "Người thuê" });
    }

    private void loadAmenityTab() {
        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) tbAmenityManage.getModel();
        m.setRowCount(0);
        for (DTO.AmenityDTO a : adminBLL.getAllAmenities())
            m.addRow(new Object[]{ a.getAmenityId(), a.getName() });
    }

    private void handleApprove(boolean approve) {
        int row = tbApproveRoom.getSelectedRow();
        if (row < 0) { javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng."); return; }
        String roomId = (String) tbApproveRoom.getValueAt(row, 0);
        if (adminBLL.updateRoomStatus(roomId, approve ? "APPROVED" : "DECLINED")) {
            javax.swing.JOptionPane.showMessageDialog(this, approve ? "Đã duyệt bài." : "Đã từ chối bài.");
            loadApproveTab();
            loadRoomManageTab(null, null);
        }
    }

    private void handleSearchRoom() {
        String keyword = txtSearchRoom.getText().trim();
        String statusStr = (String) cboStatus.getSelectedItem();
        String statusFilter = switch (statusStr) {
            case "Đã duyệt"   -> "APPROVED";
            case "Chờ duyệt"  -> "PENDING";
            case "Bị từ chối" -> "DECLINED";
            default           -> null;
        };
        loadRoomManageTab(keyword, statusFilter);
    }

    private void handleDeleteRoom() {
        int row = tbRoomManage.getSelectedRow();
        if (row < 0) { javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng."); return; }
        String roomId = (String) tbRoomManage.getValueAt(row, 0);
        int c = javax.swing.JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa bài đăng này?",
                "Xác nhận xóa", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (c == javax.swing.JOptionPane.YES_OPTION && adminBLL.deleteRoom(roomId)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Đã xóa bài đăng.");
            loadRoomManageTab(null, null);
            loadApproveTab();
        }
    }

    private void openRoomDetail(javax.swing.JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng."); return; }
        String roomId = (String) table.getValueAt(row, 0);
        DTO.RoomDTO room = adminBLL.getRoomById(roomId);
        if (room != null) new RoomDetailFrame(room, currentUser).setVisible(true);
    }

    private void handleSearchUser() {
        String keyword = txtSearchUser.getText().trim();
        String roleStr = (String) cboRole.getSelectedItem();
        DTO.UserDTO.Role roleFilter = "Chủ trọ".equals(roleStr)   ? DTO.UserDTO.Role.LANDLORD
                                    : "Người thuê".equals(roleStr) ? DTO.UserDTO.Role.TENANT : null;
        loadUserManageTab(keyword, roleFilter);
    }

    private void handleDeleteUser() {
        int row = tbUserManage.getSelectedRow();
        if (row < 0) { javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng."); return; }
        String userId = (String) tbUserManage.getValueAt(row, 0);
        int c = javax.swing.JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa người dùng này?",
                "Xác nhận xóa", javax.swing.JOptionPane.YES_NO_OPTION);
        if (c == javax.swing.JOptionPane.YES_OPTION) {
            if (adminBLL.deleteUser(userId))
                { javax.swing.JOptionPane.showMessageDialog(this, "Đã xóa người dùng."); loadUserManageTab(null, null); }
            else
                javax.swing.JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddAmenity() {
        String name = javax.swing.JOptionPane.showInputDialog(this, "Nhập tên tiện nghi mới:");
        if (name != null && !name.trim().isEmpty() && adminBLL.addAmenity(name.trim()))
            { javax.swing.JOptionPane.showMessageDialog(this, "Đã thêm tiện nghi."); loadAmenityTab(); }
    }

    private void handleUpdateAmenity() {
        int row = tbAmenityManage.getSelectedRow();
        if (row < 0) { javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng."); return; }
        String id  = (String) tbAmenityManage.getValueAt(row, 0);
        String cur = (String) tbAmenityManage.getValueAt(row, 1);
        String newName = javax.swing.JOptionPane.showInputDialog(this, "Sửa tên tiện nghi:", cur);
        if (newName != null && !newName.trim().isEmpty() && adminBLL.updateAmenity(id, newName.trim()))
            { javax.swing.JOptionPane.showMessageDialog(this, "Đã cập nhật tiện nghi."); loadAmenityTab(); }
    }

    private void handleDeleteAmenity() {
        int row = tbAmenityManage.getSelectedRow();
        if (row < 0) { javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng."); return; }
        String id = (String) tbAmenityManage.getValueAt(row, 0);
        int c = javax.swing.JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tiện nghi này?",
                "Xác nhận xóa", javax.swing.JOptionPane.YES_NO_OPTION);
        if (c == javax.swing.JOptionPane.YES_OPTION && adminBLL.deleteAmenity(id))
            { javax.swing.JOptionPane.showMessageDialog(this, "Đã xóa tiện nghi."); loadAmenityTab(); }
    }

    private static String statusToLabel(String s) {
        if (s == null) return "Không rõ";
        return switch (s) {
            case "APPROVED" -> "Đã duyệt";
            case "DECLINED" -> "Bị từ chối";
            default         -> "Chờ duyệt";
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnApproveRoom = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbApproveRoom = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        btnRoomDetail_tab1 = new javax.swing.JButton();
        btnApproveRoom = new javax.swing.JButton();
        btnDeclineRoom = new javax.swing.JButton();
        pnRoomManage = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbRoomManage = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnRoomDetail_tab2 = new javax.swing.JButton();
        btnDeleteRoom = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtSearchRoom = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cboStatus = new javax.swing.JComboBox<>();
        btnSearchRoom = new javax.swing.JButton();
        pnUserManage = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbUserManage = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtSearchUser = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboRole = new javax.swing.JComboBox<>();
        btnSearchUser = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnDeleteUser = new javax.swing.JButton();
        pnAmenityManage = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbAmenityManage = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        btnAddAmenity = new javax.swing.JButton();
        btnUpdateAmenity = new javax.swing.JButton();
        btnDeleteAmenity = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1280, 720));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Hệ thống tìm kiếm phòng trọ");

        btnLogout.setText("Đăng xuất");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 985, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogout)
                    .addComponent(jLabel1))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        pnApproveRoom.setLayout(new java.awt.BorderLayout());

        tbApproveRoom.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Tiêu đề", "Chủ trọ", "Ngày đăng", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tbApproveRoom);

        pnApproveRoom.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnRoomDetail_tab1.setText("Xem chi tiết");
        jPanel6.add(btnRoomDetail_tab1);

        btnApproveRoom.setText("Duyệt");
        jPanel6.add(btnApproveRoom);

        btnDeclineRoom.setText("Từ chối");
        jPanel6.add(btnDeclineRoom);

        pnApproveRoom.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        jTabbedPane1.addTab("Duyệt bài", pnApproveRoom);

        pnRoomManage.setLayout(new java.awt.BorderLayout());

        tbRoomManage.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Tiêu đề", "Chủ trọ", "Giá thuê", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbRoomManage);

        pnRoomManage.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnRoomDetail_tab2.setText("Xem chi tiết");
        jPanel2.add(btnRoomDetail_tab2);

        btnDeleteRoom.setText("Xóa");
        jPanel2.add(btnDeleteRoom);

        pnRoomManage.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jLabel2.setText("Tìm kiếm");
        jPanel3.add(jLabel2);

        txtSearchRoom.setMinimumSize(new java.awt.Dimension(400, 22));
        txtSearchRoom.setPreferredSize(new java.awt.Dimension(400, 22));
        jPanel3.add(txtSearchRoom);

        jLabel3.setText("Trạng thái");
        jPanel3.add(jLabel3);

        cboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Chờ duyệt", "Đã duyệt", "Bị từ chối" }));
        jPanel3.add(cboStatus);

        btnSearchRoom.setText("Tìm/Lọc");
        jPanel3.add(btnSearchRoom);

        pnRoomManage.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.addTab("Quản lý bài đăng", pnRoomManage);

        pnUserManage.setLayout(new java.awt.BorderLayout());

        tbUserManage.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Tên", "Số điện thoại", "Vai trò"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tbUserManage);

        pnUserManage.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jLabel4.setText("Tìm kiếm");
        jPanel4.add(jLabel4);

        txtSearchUser.setPreferredSize(new java.awt.Dimension(400, 22));
        jPanel4.add(txtSearchUser);

        jLabel5.setText("Vai trò");
        jPanel4.add(jLabel5);

        cboRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Chủ trọ", "Người thuê" }));
        jPanel4.add(cboRole);

        btnSearchUser.setText("Tìm/Lọc");
        jPanel4.add(btnSearchUser);

        pnUserManage.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnDeleteUser.setText("Xóa");
        jPanel5.add(btnDeleteUser);

        pnUserManage.add(jPanel5, java.awt.BorderLayout.PAGE_END);

        jTabbedPane1.addTab("Quản lý người dùng", pnUserManage);

        pnAmenityManage.setLayout(new java.awt.BorderLayout());

        tbAmenityManage.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "ID", "Tên tiện nghi"
            }
        ));
        jScrollPane4.setViewportView(tbAmenityManage);

        pnAmenityManage.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnAddAmenity.setText("Thêm");
        jPanel7.add(btnAddAmenity);

        btnUpdateAmenity.setText("Sửa");
        jPanel7.add(btnUpdateAmenity);

        btnDeleteAmenity.setText("Xóa");
        jPanel7.add(btnDeleteAmenity);

        pnAmenityManage.add(jPanel7, java.awt.BorderLayout.PAGE_END);

        jTabbedPane1.addTab("Quản lý tiện nghi", pnAmenityManage);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new AdminMainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddAmenity;
    private javax.swing.JButton btnApproveRoom;
    private javax.swing.JButton btnDeclineRoom;
    private javax.swing.JButton btnDeleteAmenity;
    private javax.swing.JButton btnDeleteRoom;
    private javax.swing.JButton btnDeleteUser;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRoomDetail_tab1;
    private javax.swing.JButton btnRoomDetail_tab2;
    private javax.swing.JButton btnSearchRoom;
    private javax.swing.JButton btnSearchUser;
    private javax.swing.JButton btnUpdateAmenity;
    private javax.swing.JComboBox<String> cboRole;
    private javax.swing.JComboBox<String> cboStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel pnAmenityManage;
    private javax.swing.JPanel pnApproveRoom;
    private javax.swing.JPanel pnRoomManage;
    private javax.swing.JPanel pnUserManage;
    private javax.swing.JTable tbAmenityManage;
    private javax.swing.JTable tbApproveRoom;
    private javax.swing.JTable tbRoomManage;
    private javax.swing.JTable tbUserManage;
    private javax.swing.JTextField txtSearchRoom;
    private javax.swing.JTextField txtSearchUser;
    // End of variables declaration//GEN-END:variables
}
