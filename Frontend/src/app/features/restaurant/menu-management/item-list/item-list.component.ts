import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MenuService, MenuItem, Category } from '../../../../core/services/menu.service';
import { RestaurantService } from '../../../../core/services/restaurant.service';

@Component({
  selector: 'app-item-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css']
})
export class ItemListComponent implements OnInit {
  items: MenuItem[] = [];
  categories: Category[] = [];
  filterCategoryId: number | null = null;
  restaurantId: number | null = null;


  newItem: Partial<MenuItem> = { name: '', price: 0, categoryId: 0, description: '', imageUrl: '' };

  constructor(
    private menu: MenuService,
    private restService: RestaurantService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.restService.getMyRestaurant().subscribe({
      next: (res: any) => {
        if (res && res.id) {
          this.restaurantId = res.id;
          this.loadCategories();
          this.loadItems(); 
        }
      },
      error: (err) => console.error('Failed to load restaurant', err)
    });

    this.route.queryParams.subscribe(p => {
      this.filterCategoryId = p['categoryId'] ? +p['categoryId'] : null;
      if (this.filterCategoryId) {
        this.newItem.categoryId = this.filterCategoryId;
        this.loadItems();
      }
    });
  }

  loadCategories() {
    if (!this.restaurantId) return;
    this.menu.getCategories(this.restaurantId).subscribe(c => (this.categories = c));
  }

  loadItems() {
    if (this.filterCategoryId) {
      this.menu.getItemsByCategory(this.filterCategoryId).subscribe(i => this.items = i);
    } else if (this.restaurantId) {
      this.menu.getItemsByRestaurant(this.restaurantId).subscribe(i => this.items = i);
    }
  }

  addItem() {
    if (!this.newItem.name?.trim() || !this.newItem.price || !this.newItem.categoryId) {
      alert('Please fill in name, price and select a category');
      return;
    }

    this.menu.addItem(this.newItem).subscribe({
      next: () => {
        this.newItem = { name: '', price: 0, categoryId: this.filterCategoryId || 0, description: '', imageUrl: '' };
        this.loadItems();
      },
      error: (err) => console.error('Failed to add item', err)
    });
  }

  deleteItem(item: MenuItem) {
    if (confirm(`Delete "${item.name}"?`)) {
      this.menu.deleteItem(item.id).subscribe(() => this.loadItems());
    }
  }

  startEdit(item: MenuItem) {
    (item as any).editing = true;
    (item as any).editName = item.name;
    (item as any).editPrice = item.price;
    (item as any).editCategoryId = item.categoryId;
    (item as any).editDescription = item.description;
    (item as any).editImageUrl = item.imageUrl;
  }

  saveEdit(item: MenuItem) {
    const edit = item as any;
    this.menu
      .updateItem(item.id, {
        name: edit.editName,
        price: edit.editPrice,
        categoryId: edit.editCategoryId,
        description: edit.editDescription,
        imageUrl: edit.editImageUrl
      })
      .subscribe(() => {
        delete (item as any).editing;
        this.loadItems();
      });
  }

  cancelEdit(item: MenuItem) {
    delete (item as any).editing;
  }

  getCategoryName(id: number): string {
    return this.categories.find(c => c.id === id)?.name || 'Unknown';
  }
}
