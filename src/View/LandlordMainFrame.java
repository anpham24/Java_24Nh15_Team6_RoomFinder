/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

/**
 *
 * @author anpha
 */
public class LandlordMainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LandlordMainFrame.class.getName());

    private DTO.UserDTO currentUser;

    private final BLL.LandlordController landlordBLL = new BLL.LandlordController();

    public LandlordMainFrame(DTO.UserDTO user) {
        this.currentUser = user;
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        initEvents();
        loadRooms();
    }

    public LandlordMainFrame() { this(null); }

    public DTO.UserDTO getCurrentUser() { return currentUser; }

    private void initEvents() {
        btnAddRoom.addActionListener(e ->
            new RoomActionDialog(this, currentUser, null, this::loadRooms).setVisible(true));
        btnLogout.addActionListener(e -> {
            int c = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?", "Đăng xuất",
                    javax.swing.JOptionPane.YES_NO_OPTION);
            if (c == javax.swing.JOptionPane.YES_OPTION) {
                dispose();
                new TenantMainFrame().setVisible(true);
            }
        });
    }

    private void loadRooms() {
        pnRoomList.removeAll();
        if (currentUser == null) { pnRoomList.revalidate(); return; }
        for (DTO.RoomDTO room : landlordBLL.getLandlordRooms(currentUser.getUserId()))
            pnRoomList.add(buildCard(room, landlordBLL.getAverageRating(room.getRoomId())));
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
        card.getLbStatus().setText(statusToLabel(room.getStatus()));
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
        card.getBtnUpdate().addActionListener(e ->
            new RoomActionDialog(this, currentUser, room, this::loadRooms).setVisible(true));
        card.getBtnDelete().addActionListener(e -> {
            int c = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa phòng \"" + room.getTitle() + "\"?",
                    "Xác nhận xóa", javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            if (c == javax.swing.JOptionPane.YES_OPTION) {
                if (landlordBLL.deleteRoom(room.getRoomId()))
                    { javax.swing.JOptionPane.showMessageDialog(this, "Đã xóa phòng."); loadRooms(); }
                else
                    javax.swing.JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        card.getBtnAvailability().addActionListener(e -> {
            if (landlordBLL.updateAvailability(room.getRoomId(), !room.isAvailability())) {
                room.setAvailability(!room.isAvailability());
                card.getLbAvailability().setText(room.isAvailability() ? "Còn phòng" : "Hết phòng");
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnRoomList = new javax.swing.JPanel();
        btnAddRoom = new javax.swing.JButton();

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
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogout)
                    .addComponent(jLabel1))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel2.setText("Quản lý phòng trọ");

        pnRoomList.setLayout(new java.awt.GridLayout(0, 3, 20, 20));
        jScrollPane1.setViewportView(pnRoomList);

        btnAddRoom.setText("Thêm phòng mới");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddRoom))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnAddRoom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                .addContainerGap())
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
        java.awt.EventQueue.invokeLater(() -> new LandlordMainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRoom;
    private javax.swing.JButton btnLogout;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnRoomList;
    // End of variables declaration//GEN-END:variables
}
