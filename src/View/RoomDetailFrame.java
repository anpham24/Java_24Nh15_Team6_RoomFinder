/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

/**
 *
 * @author anpha
 */
public class RoomDetailFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RoomDetailFrame.class.getName());

    private DTO.UserDTO currentUser;
    private DTO.RoomDTO room;

    private final BLL.RoomDetailController roomDetailBLL = new BLL.RoomDetailController();

    public RoomDetailFrame(DTO.RoomDTO room, DTO.UserDTO user) {
        this.room = room;
        this.currentUser = user;
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        if (room == null) return;
        populateRoomInfo();
        loadImages();
        loadAmenities();
        loadReviews();
        applyRoleVisibility();
        initEvents();
    }

    public RoomDetailFrame() { this(null, null); }

    public DTO.UserDTO getCurrentUser() { return currentUser; }
    public DTO.RoomDTO getRoom()        { return room; }

    private void populateRoomInfo() {
        lbTitle.setText(room.getTitle());
        lbAvailability.setText(room.isAvailability() ? "Còn phòng" : "Hết phòng");
        lbStatus.setText(statusToLabel(room.getStatus()));
        lbDescription.setText("<html><body style='width:430px'>" + room.getDescription() + "</body></html>");
        lbAddress.setText(room.getAddress());
        lbPrice.setText(String.format("%,.0f VNĐ/tháng", room.getPrice()));
        lbArea.setText(room.getArea() + " m²");
        lbRating.setText(String.format("⭐ %.1f", roomDetailBLL.getAverageRating(room.getRoomId())));
        DTO.UserDTO landlord = roomDetailBLL.getUserById(room.getLandlordId());
        lbPhone.setText(landlord != null ? landlord.getPhoneNumber() : "");
    }

    private void loadImages() {
        java.util.List<String> paths = room.getImagePathList();
        if (paths == null || paths.isEmpty()) return;
        setMainImage(paths.get(0));
        pnImageList.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
        pnImageList.removeAll();
        for (String path : paths) {
            javax.swing.JLabel thumb = new javax.swing.JLabel();
            try {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(path);
                java.awt.Image scaled = icon.getImage().getScaledInstance(100, 80, java.awt.Image.SCALE_SMOOTH);
                thumb.setIcon(new javax.swing.ImageIcon(scaled));
            } catch (Exception ignored) { thumb.setText("[ảnh]"); }
            thumb.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            thumb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) { setMainImage(path); }
            });
            pnImageList.add(thumb);
        }
        pnImageList.revalidate();
        pnImageList.repaint();
    }

    private void setMainImage(String path) {
        try {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(path);
            java.awt.Image scaled = icon.getImage().getScaledInstance(550, 560, java.awt.Image.SCALE_SMOOTH);
            lbMainImage.setIcon(new javax.swing.ImageIcon(scaled));
            lbMainImage.setText("");
        } catch (Exception ignored) {}
    }

    private void loadAmenities() {
        pnAmenity.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        pnAmenity.removeAll();
        for (DTO.AmenityDTO a : room.getAmenityList())
            pnAmenity.add(new javax.swing.JLabel("✓ " + a.getName()));
        pnAmenity.revalidate();
        pnAmenity.repaint();
    }

    private void loadReviews() {
        pnReviewList.setLayout(new javax.swing.BoxLayout(pnReviewList, javax.swing.BoxLayout.Y_AXIS));
        pnReviewList.removeAll();
        for (DTO.ReviewDTO rv : roomDetailBLL.getReviews(room.getRoomId())) {
            ReviewPanel panel = new ReviewPanel();
            DTO.UserDTO tenant = roomDetailBLL.getUserById(rv.getTenantId());
            panel.getLbName().setText(tenant != null ? tenant.getName() : rv.getTenantId());
            panel.getLbContent().setText("<html><body style='width:450px'>" + rv.getComment() + "</body></html>");
            panel.getLbRating().setText("⭐ " + rv.getRating() + "/5");
            pnReviewList.add(panel);
        }
        pnReviewList.revalidate();
        pnReviewList.repaint();
    }

    private void applyRoleVisibility() {
        if (currentUser == null) {
            btnUpdate.setVisible(false);
            btnDelete.setVisible(false);
            btnAvailability.setVisible(false);
            lbStatus.setVisible(false);
            txtReview.setVisible(true);
            cboRating.setVisible(true);
            lbStar.setVisible(true);
            btnSubmit.setText("Đăng nhập để đánh giá");
            btnSubmit.setVisible(true);
            lbPhone.setVisible(true);
            return;
        }
        switch (currentUser.getRole()) {
            case TENANT -> {
                btnUpdate.setVisible(false);
                btnDelete.setVisible(false);
                btnAvailability.setVisible(false);
                lbStatus.setVisible(false);
                txtReview.setVisible(true);
                cboRating.setVisible(true);
                lbStar.setVisible(true);
                btnSubmit.setVisible(true);
                lbPhone.setVisible(true);
            }
            case LANDLORD -> {
                boolean isOwner = room.getLandlordId().equals(currentUser.getUserId());
                btnUpdate.setVisible(isOwner);
                btnDelete.setVisible(isOwner);
                btnAvailability.setVisible(isOwner);
                lbStatus.setVisible(true);
                txtReview.setVisible(false);
                cboRating.setVisible(false);
                lbStar.setVisible(false);
                btnSubmit.setVisible(false);
            }
            case ADMIN -> {
                btnUpdate.setVisible(false);
                btnDelete.setVisible(true);
                btnAvailability.setVisible(false);
                lbStatus.setVisible(true);
                txtReview.setVisible(false);
                cboRating.setVisible(false);
                lbStar.setVisible(false);
                btnSubmit.setVisible(false);
            }
        }
    }

    private void initEvents() {
        btnSubmit.addActionListener(e -> handleSubmitReview());
        btnUpdate.addActionListener(e -> {
            new RoomActionDialog(this, currentUser, room, () -> {
                room = roomDetailBLL.refreshRoom(room.getRoomId());
                if (room != null) { populateRoomInfo(); loadImages(); loadAmenities(); }
            }).setVisible(true);
        });
        btnDelete.addActionListener(e -> {
            int c = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa phòng này?", "Xác nhận xóa",
                    javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
            if (c == javax.swing.JOptionPane.YES_OPTION) {
                if (roomDetailBLL.deleteRoom(room.getRoomId()))
                    { javax.swing.JOptionPane.showMessageDialog(this, "Đã xóa phòng."); dispose(); }
                else
                    javax.swing.JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        btnAvailability.addActionListener(e -> {
            if (roomDetailBLL.updateAvailability(room.getRoomId(), !room.isAvailability())) {
                room.setAvailability(!room.isAvailability());
                lbAvailability.setText(room.isAvailability() ? "Còn phòng" : "Hết phòng");
            }
        });
    }

    private void handleSubmitReview() {
        if (currentUser == null) {
            int c = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn cần đăng nhập để đánh giá phòng này.\nBạn có muốn đăng nhập không?",
                    "Yêu cầu đăng nhập", javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            if (c == javax.swing.JOptionPane.YES_OPTION) {
                for (java.awt.Window w : java.awt.Window.getWindows()) w.dispose();
                new LoginFrame().setVisible(true);
            }
            return;
        }
        String content = txtReview.getText().trim();
        if (content.isEmpty() || content.equals("<Nhập đánh giá>")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung đánh giá.");
            return;
        }
        int rating;
        try { rating = Integer.parseInt((String) cboRating.getSelectedItem()); }
        catch (Exception ex) { rating = 5; }
        String error = roomDetailBLL.submitReview(room.getRoomId(), currentUser.getUserId(), rating, content);
        if (error == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Đánh giá thành công!");
            txtReview.setText("");
            loadReviews();
            lbRating.setText(String.format("⭐ %.1f", roomDetailBLL.getAverageRating(room.getRoomId())));
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, error, "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String statusToLabel(String s) {
        if (s == null) return "⏳ Chờ duyệt";
        return switch (s) {
            case "APPROVED" -> "✔ Đã duyệt";
            case "DECLINED" -> "✘ Bị từ chối";
            default         -> "⏳ Chờ duyệt";
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

        lbMainImage = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnImageList = new javax.swing.JPanel();
        lbAvailability = new javax.swing.JLabel();
        lbTitle = new javax.swing.JLabel();
        lbPrice = new javax.swing.JLabel();
        lbArea = new javax.swing.JLabel();
        lbAddress = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pnAmenity = new javax.swing.JPanel();
        lbRating = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lbDescription = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbPhone = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnReviewList = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtReview = new javax.swing.JTextField();
        cboRating = new javax.swing.JComboBox<>();
        btnSubmit = new javax.swing.JButton();
        lbStar = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        lbStatus = new javax.swing.JLabel();
        btnAvailability = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new java.awt.Dimension(1280, 720));

        lbMainImage.setText("jLabel1");

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(pnImageList);

        lbAvailability.setText("<Còn/hết>");

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbTitle.setText("<Tiêu đề>");

        lbPrice.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbPrice.setForeground(new java.awt.Color(255, 0, 51));
        lbPrice.setText("<Giá tiền>");

        lbArea.setForeground(new java.awt.Color(153, 153, 153));
        lbArea.setText("<Diện tích>");

        lbAddress.setText("<Địa chỉ>");

        jLabel4.setText("Tiện nghi");

        pnAmenity.setBackground(java.awt.SystemColor.controlHighlight);
        pnAmenity.setMinimumSize(new java.awt.Dimension(30, 30));
        pnAmenity.setLayout(new java.awt.GridLayout(1, 0, 15, 10));

        lbRating.setText("<Rating>");

        jLabel1.setText("Mô tả");

        lbDescription.setText("<Mô tả>");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Liên hệ");

        lbPhone.setText("<Số điện thoại chủ trọ>");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        javax.swing.GroupLayout pnReviewListLayout = new javax.swing.GroupLayout(pnReviewList);
        pnReviewList.setLayout(pnReviewListLayout);
        pnReviewListLayout.setHorizontalGroup(
            pnReviewListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );
        pnReviewListLayout.setVerticalGroup(
            pnReviewListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(pnReviewList);

        jLabel3.setText("Đánh giá");

        txtReview.setText("<Nhập đánh giá>");

        cboRating.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5", "4", "3", "2", "1" }));

        btnSubmit.setText("Đăng");

        lbStar.setText("sao");

        btnUpdate.setText("Sửa bài");

        btnDelete.setText("Xóa bài");

        lbStatus.setText("<Chờ duyệt/đã duyệt>");

        btnAvailability.setText("Đổi còn/hết");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbMainImage, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbAvailability, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(lbDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbRating)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbStatus))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbAddress)
                                    .addComponent(jLabel3))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(lbPhone)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAvailability)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDelete)
                        .addGap(36, 36, 36))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtReview, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboRating, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbStar)
                                .addGap(12, 12, 12)
                                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnAmenity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbPrice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbArea)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbMainImage, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbAvailability))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbRating)
                            .addComponent(lbStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnAmenity, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbAddress)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbPrice)
                            .addComponent(lbArea))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lbPhone)
                            .addComponent(btnUpdate)
                            .addComponent(btnDelete)
                            .addComponent(btnAvailability))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtReview, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboRating, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbStar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26))
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
        java.awt.EventQueue.invokeLater(() -> new RoomDetailFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAvailability;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cboRating;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbAddress;
    private javax.swing.JLabel lbArea;
    private javax.swing.JLabel lbAvailability;
    private javax.swing.JLabel lbDescription;
    private javax.swing.JLabel lbMainImage;
    private javax.swing.JLabel lbPhone;
    private javax.swing.JLabel lbPrice;
    private javax.swing.JLabel lbRating;
    private javax.swing.JLabel lbStar;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnAmenity;
    private javax.swing.JPanel pnImageList;
    private javax.swing.JPanel pnReviewList;
    private javax.swing.JTextField txtReview;
    // End of variables declaration//GEN-END:variables
}
