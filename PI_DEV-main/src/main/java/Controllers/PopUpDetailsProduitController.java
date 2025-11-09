package Controllers;
import Models.etat;
import Models.panier;
import Models.produit;
import Services.PanierProduitService;
import Services.panierService;
import Services.produitService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PopUpDetailsProduitController {
    @FXML
    private Label categorie;
    @FXML
    private Label description;
    @FXML
    private Label disponibilité;
    @FXML
    private ImageView ViewImage;
    @FXML
    private Label nom_produit;
    @FXML
    private Label prix;
    @FXML
    private Spinner<Integer> quantite;
    private produit produitSelectionne;

    public void setProductDetails(String nom, String cat, String desc, String dispo, float prixValue, String imagePath, int stock) {
        // Remplir les labels
        nom_produit.setText(nom);
        categorie.setText(cat);

        // Troncation du texte de description si trop long (limite à 100 caractères par exemple)
        String truncatedDesc = desc.length() > 40 ? desc.substring(0, 40) + "..." : desc;
        description.setText(truncatedDesc);

        // Ajouter un Tooltip pour la description complète
        Tooltip descTooltip = new Tooltip(desc);
        Tooltip.install(description, descTooltip);

        disponibilité.setText(dispo);
        prix.setText(prixValue + " TND");

        // Charger l'image
        Image image = new Image(imagePath);
        ViewImage.setImage(image);

        // Configurer le Spinner pour la quantité
        quantite.setValueFactory(new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(1, stock, 1));
    }

    @FXML
    public void AjouterProduitAupanier() {
        try {
            produitService produitService = new produitService();
            // Récupérer l'utilisateur connecté
            int idUser = Session.getInstance().getCurrentUser().getId();
            int qte = quantite.getValue();

            // Créer ou récupérer le panier de l'utilisateur
            panierService panierService = new panierService();
            panier panierUtilisateur = panierService.getOrCreatePanierForUser(idUser);

            if (panierUtilisateur == null) {
                throw new IllegalStateException("Le panier n'a pas pu être créé ou récupéré.");
            }

            // Vérification que le produit est sélectionné et que l'ID est valide
            if (produitSelectionne == null || produitSelectionne.getId() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Aucun produit sélectionné ou ID invalide.", ButtonType.OK);
                alert.show();
                return;
            }

            // Vérification de la disponibilité du stock
            if (qte > produitSelectionne.getQuantite()) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "La quantité demandée dépasse le stock disponible (" + produitSelectionne.getQuantite() + ").",
                        ButtonType.OK);
                alert.show();
                return;
            }

            // Ajouter le produit au panier
            PanierProduitService panierProduitService = new PanierProduitService();
            panierProduitService.ajouterProduitAuPanier(panierUtilisateur, produitSelectionne, qte);

            // Décrémenter le stock du produit
            int nouvelleQuantite = produitSelectionne.getQuantite() - qte;
            produitSelectionne.setQuantite(nouvelleQuantite);

            // Vérification de l'état du produit après la mise à jour de la quantité
            if (nouvelleQuantite == 0) {
                // Si la quantité est 0, changer l'état du produit à "Rupture"
                produitSelectionne.setEtat(etat.Rupture);
            }

            // Mise à jour dans la base de données pour refléter les changements de quantité et d'état
            produitService.update(produitSelectionne);
            // Actualiser l'affichage du panier si disponible
            PanierController panierController = PanierController.getInstance();
            if (panierController != null) {
                panierController.click_on_Panier(new ActionEvent());
            } else {
                System.out.println("PanierController est nul !");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Produit ajouté au panier avec succès.",
                    ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage(), ButtonType.OK);
            alert.show();
            e.printStackTrace();
        }
    }

    public void setProduitSelectionne(produit produit) {
        if (produit != null) {
            this.produitSelectionne = produit;
        }
    }

}
