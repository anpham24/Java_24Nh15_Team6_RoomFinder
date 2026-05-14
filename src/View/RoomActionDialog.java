/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package View;

/**
 *
 * @author anpha
 */
public class RoomActionDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RoomActionDialog.class.getName());

    /**
     * Creates new form RoomActionDialog
     */
    private DTO.UserDTO currentUser;
    private DTO.RoomDTO existingRoom;   // null = thêm mới, non-null = cập nhật
    private Runnable onSaveCallback;    // callback để reload danh sách phòng

    private final BLL.RoomActionController roomActionBLL = new BLL.RoomActionController();
    private final java.util.List<javax.swing.JCheckBox> amenityCheckboxes = new java.util.ArrayList<>();
    private final java.util.List<String> selectedImagePaths = new java.util.ArrayList<>();

    public RoomActionDialog(java.awt.Frame parent, DTO.UserDTO user,
                            DTO.RoomDTO room, Runnable onSave) {
        super(parent, true);
        this.currentUser    = user;
        this.existingRoom   = room;
        this.onSaveCallback = onSave;
        initComponents();
        this.setLocationRelativeTo(null);
        initAmenityCheckboxes();
        if (room != null) prefillForm();
        initEvents();
    }

    public RoomActionDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
    }

    private void initAmenityCheckboxes() {
        pnAmenity.removeAll();
        amenityCheckboxes.clear();
        for (DTO.AmenityDTO a : roomActionBLL.getAllAmenities()) {
            javax.swing.JCheckBox cb = new javax.swing.JCheckBox(a.getName());
            cb.putClientProperty("amenityId", a.getAmenityId());
            amenityCheckboxes.add(cb);
            pnAmenity.add(cb);
        }
        pnAmenity.revalidate();
    }

    private void prefillForm() {
        txtTitle.setText(existingRoom.getTitle());
        txtDescription.setText(existingRoom.getDescription());
        txtAddress.setText(existingRoom.getAddress());
        txtPrice.setText(String.valueOf((int) existingRoom.getPrice()));
        txtArea.setText(String.valueOf(existingRoom.getArea()));
        java.util.List<String> roomAmenityIds = new java.util.ArrayList<>();
        for (DTO.AmenityDTO a : existingRoom.getAmenityList()) roomAmenityIds.add(a.getAmenityId());
        for (javax.swing.JCheckBox cb : amenityCheckboxes)
            cb.setSelected(roomAmenityIds.contains((String) cb.getClientProperty("amenityId")));
        selectedImagePaths.addAll(existingRoom.getImagePathList());
        renderImagePreviews();
    }

    private void initEvents() {
        btnBrowse.addActionListener(e -> handleBrowseImage());
        btnSave.addActionListener(e -> handleSave());
        btnExit.addActionListener(e -> dispose());
    }

    private void handleBrowseImage() {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif", "bmp"));
        if (fc.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
        java.util.List<String> errors = new java.util.ArrayList<>();
        for (String p : roomActionBLL.copyImagesToProject(fc.getSelectedFiles(), errors))
            if (!selectedImagePaths.contains(p)) selectedImagePaths.add(p);
        if (!errors.isEmpty())
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Không thể sao chép các ảnh sau:\n" + String.join("\n", errors),
                    "Lỗi sao chép ảnh", javax.swing.JOptionPane.WARNING_MESSAGE);
        renderImagePreviews();
    }

    private void handleSave() {
        java.util.List<DTO.AmenityDTO> selected = new java.util.ArrayList<>();
        for (javax.swing.JCheckBox cb : amenityCheckboxes)
            if (cb.isSelected())
                selected.add(new DTO.AmenityDTO((String) cb.getClientProperty("amenityId"), cb.getText()));
        String existingId = existingRoom != null ? existingRoom.getRoomId() : null;
        String error = roomActionBLL.saveRoom(
                txtTitle.getText().trim(), txtAddress.getText().trim(),
                txtDescription.getText().trim(), txtPrice.getText().trim(), txtArea.getText().trim(),
                new java.util.ArrayList<>(selectedImagePaths), selected,
                currentUser.getUserId(), existingId);
        if (error == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    existingId == null ? "Thêm phòng thành công! Chờ Admin duyệt." : "Cập nhật thành công.");
            dispose();
            if (onSaveCallback != null) onSaveCallback.run();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, error, "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderImagePreviews() {
        pnImageList.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
        pnImageList.removeAll();
        for (String path : selectedImagePaths) {
            javax.swing.JLabel thumb = new javax.swing.JLabel();
            try {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(path);
                java.awt.Image scaled = icon.getImage().getScaledInstance(100, 80, java.awt.Image.SCALE_SMOOTH);
                thumb.setIcon(new javax.swing.ImageIcon(scaled));
            } catch (Exception ignored) { thumb.setText("[ảnh]"); }
            thumb.setToolTipText("Double-click để xóa ảnh");
            final String p = path;
            thumb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) { selectedImagePaths.remove(p); renderImagePreviews(); }
                }
            });
            pnImageList.add(thumb);
        }
        pnImageList.revalidate();
        pnImageList.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pnAmenity = new javax.swing.JPanel();
        txtTitle = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtArea = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        pnImageList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new java.awt.Dimension(1280, 720));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Thêm/sửa phòng");

        jLabel2.setText("Tiêu đề");

        jLabel3.setText("Mô tả");

        jLabel4.setText("Tiện nghi");

        pnAmenity.setLayout(new java.awt.GridLayout(0, 3, 20, 20));

        jLabel5.setText("Địa chỉ");

        jLabel6.setText("Giá tiền");

        jLabel7.setText("Diện tích");

        btnBrowse.setText("Chọn ảnh");

        btnExit.setText("Hủy");

        btnSave.setText("Lưu");

        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtDescription);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(253, 253, 253)
                            .addComponent(jLabel1))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(34, 34, 34)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(22, 22, 22)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtTitle)
                                        .addComponent(jScrollPane1)))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(108, 108, 108)
                                    .addComponent(btnExit)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSave)
                                    .addGap(128, 128, 128))))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(32, 32, 32)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtPrice)
                                            .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtArea))
                                    .addComponent(pnImageList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnAmenity, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(267, 267, 267)
                        .addComponent(btnBrowse)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(pnAmenity, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnImageList, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBrowse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExit)
                    .addComponent(btnSave))
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                RoomActionDialog dialog = new RoomActionDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnAmenity;
    private javax.swing.JPanel pnImageList;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtArea;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
}
