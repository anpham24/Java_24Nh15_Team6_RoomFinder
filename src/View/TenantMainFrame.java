/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

/**
 *
 * @author anpha
 */
public class TenantMainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TenantMainFrame.class.getName());

    private DTO.UserDTO currentUser;

    private final BLL.TenantController tenantBLL = new BLL.TenantController();
    private final java.util.List<javax.swing.JCheckBox> amenityCheckboxes = new java.util.ArrayList<>();

    public TenantMainFrame(DTO.UserDTO user) {
        this.currentUser = user;
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        initAmenityCheckboxes();
        initEvents();
        handleApply();
        if (currentUser == null) btnLogout.setText("Đăng nhập");
    }

    /** Constructor mặc định (giữ để Netbeans form editor không báo lỗi) */
    public TenantMainFrame() { this(null); }

    public DTO.UserDTO getCurrentUser() { return currentUser; }

    private void initAmenityCheckboxes() {
        pnAmenity.removeAll();
        amenityCheckboxes.clear();
        for (DTO.AmenityDTO a : tenantBLL.getAllAmenities()) {
            javax.swing.JCheckBox cb = new javax.swing.JCheckBox(a.getName());
            cb.putClientProperty("amenityId", a.getAmenityId());
            amenityCheckboxes.add(cb);
            pnAmenity.add(cb);
        }
        pnAmenity.revalidate();
    }

    private void initEvents() {
        btnSearch.addActionListener(e -> handleSearch());
        btnApply.addActionListener(e -> handleApply());
        btnLogout.addActionListener(e -> handleLogout());
        txtSearch.addActionListener(e -> handleSearch());
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        loadRooms(tenantBLL.getRooms(keyword, 0, Double.MAX_VALUE,
                java.util.Collections.emptyList(), false, false));
    }

    private void handleApply() {
        String keyword  = txtSearch.getText().trim();
        double minPrice = parseDouble(txtMinPrice.getText().trim(), 0);
        double maxPrice = parseDouble(txtMaxPrice.getText().trim(), Double.MAX_VALUE);
        java.util.List<String> ids = new java.util.ArrayList<>();
        for (javax.swing.JCheckBox cb : amenityCheckboxes)
            if (cb.isSelected()) ids.add((String) cb.getClientProperty("amenityId"));
        loadRooms(tenantBLL.getRooms(keyword, minPrice, maxPrice, ids,
                rdoPrice.isSelected(), rdoReview.isSelected()));
    }

    private void handleLogout() {
        if (currentUser == null) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }
        int c = javax.swing.JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?", "Đăng xuất",
                javax.swing.JOptionPane.YES_NO_OPTION);
        if (c == javax.swing.JOptionPane.YES_OPTION) {
            dispose();
            new TenantMainFrame().setVisible(true);
        }
    }

    private void loadRooms(java.util.List<DTO.RoomDTO> rooms) {
        pnRoomList.removeAll();
        for (DTO.RoomDTO room : rooms)
            pnRoomList.add(buildCard(room, tenantBLL.getAverageRating(room.getRoomId())));
        pnRoomList.revalidate();
        pnRoomList.repaint();
    }

    private RoomCardPanel buildCard(DTO.RoomDTO room, double avgRating) {
        RoomCardPanel card = new RoomCardPanel();
        card.getLbTitle().setText(room.getTitle());
        card.getLbPrice().setText(String.format("%,.0f VNĐ/tháng", room.getPrice()));
        card.getLbArea().setText(room.getArea() + " m²");
        card.getLbAddress().setText(truncate(room.getAddress(), 40));
        card.getLbAvailability().setText(room.isAvailability() ? "Còn phòng" : "Hết phòng");
        card.getLbRating().setText(String.format("⭐ %.1f", avgRating));
        card.getBtnUpdate().setVisible(false);
        card.getBtnDelete().setVisible(false);
        card.getBtnAvailability().setVisible(false);
        card.getLbStatus().setVisible(false);
        javax.swing.JPanel pnA = card.getPnAmenity();
        pnA.removeAll();
        java.util.List<DTO.AmenityDTO> ams = room.getAmenityList();
        for (int i = 0; i < Math.min(3, ams.size()); i++)
            pnA.add(new javax.swing.JLabel(ams.get(i).getName()));
        loadThumbnail(card, room);
        card.getLbThumb().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new RoomDetailFrame(room, currentUser).setVisible(true);
            }
        });
        card.getLbTitle().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new RoomDetailFrame(room, currentUser).setVisible(true);
            }
        });
        return card;
    }

    private void loadThumbnail(RoomCardPanel card, DTO.RoomDTO room) {
        java.util.List<String> imgs = room.getImagePathList();
        if (imgs == null || imgs.isEmpty()) return;
        try {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imgs.get(0));
            java.awt.Image scaled = icon.getImage().getScaledInstance(380, 200, java.awt.Image.SCALE_SMOOTH);
            card.getLbThumb().setIcon(new javax.swing.ImageIcon(scaled));
            card.getLbThumb().setText("");
        } catch (Exception ignored) {}
    }

    private String truncate(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max) + "…";
    }

    private double parseDouble(String s, double fallback) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return fallback; }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btgSort = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        pnSort = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        rdoPrice = new javax.swing.JRadioButton();
        rdoReview = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        txtMinPrice = new javax.swing.JTextField();
        txtMaxPrice = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnApply = new javax.swing.JButton();
        pnAmenity = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnRoomList = new javax.swing.JPanel();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        pnSort.setBackground(new java.awt.Color(255, 255, 255));
        pnSort.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("Sắp xếp theo");

        btgSort.add(rdoPrice);
        rdoPrice.setText("Giá tiền");

        btgSort.add(rdoReview);
        rdoReview.setText("Đánh giá");

        jLabel3.setText("Khoảng giá");

        jLabel4.setText("Tiện nghi");

        btnApply.setText("Áp dụng");

        pnAmenity.setBackground(new java.awt.Color(255, 255, 255));
        pnAmenity.setLayout(new java.awt.GridLayout(0, 3, 15, 10));

        javax.swing.GroupLayout pnSortLayout = new javax.swing.GroupLayout(pnSort);
        pnSort.setLayout(pnSortLayout);
        pnSortLayout.setHorizontalGroup(
            pnSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSortLayout.createSequentialGroup()
                .addGroup(pnSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnSortLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(pnSortLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(rdoPrice)
                        .addGap(18, 18, 18)
                        .addComponent(rdoReview)))
                .addGroup(pnSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnSortLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel3))
                    .addGroup(pnSortLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(txtMinPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtMaxPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31)
                .addGroup(pnSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnSortLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addContainerGap())
                    .addGroup(pnSortLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(pnAmenity, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnApply, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        pnSortLayout.setVerticalGroup(
            pnSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSortLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnSortLayout.createSequentialGroup()
                        .addGroup(pnSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoPrice)
                            .addComponent(rdoReview)
                            .addComponent(txtMinPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaxPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addComponent(btnApply)
                        .addGap(0, 67, Short.MAX_VALUE))
                    .addComponent(pnAmenity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        btnSearch.setText("Tìm kiếm");

        pnRoomList.setLayout(new java.awt.GridLayout(0, 3, 20, 20));
        jScrollPane1.setViewportView(pnRoomList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnSort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        java.awt.EventQueue.invokeLater(() -> new TenantMainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btgSort;
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnAmenity;
    private javax.swing.JPanel pnRoomList;
    private javax.swing.JPanel pnSort;
    private javax.swing.JRadioButton rdoPrice;
    private javax.swing.JRadioButton rdoReview;
    private javax.swing.JTextField txtMaxPrice;
    private javax.swing.JTextField txtMinPrice;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
