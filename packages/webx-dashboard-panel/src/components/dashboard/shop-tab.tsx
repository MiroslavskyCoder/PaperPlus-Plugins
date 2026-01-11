"use client";

import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { ShoppingCart, Plus, Trash2, Edit, DollarSign, Package } from 'lucide-react';

interface ShopItem {
  id: string;
  name: string;
  material: string;
  price: number;
  icon: string | null;
}

export function ShopTab() {
  const [items, setItems] = useState<ShopItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [editingItem, setEditingItem] = useState<ShopItem | null>(null);
  
  const [formData, setFormData] = useState({
    name: '',
    material: '',
    price: '',
    icon: ''
  });

  useEffect(() => {
    loadShopItems();
  }, []);

  const loadShopItems = async () => {
    setLoading(true);
    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const response = await fetch(`${protocol}//${host}/api/shop`);
      const data = await response.json();
      
      if (data.success) {
        setItems(data.data || []);
      }
    } catch (err) {
      setError('Failed to load shop items');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const item = {
        id: editingItem?.id || Date.now().toString(),
        name: formData.name,
        material: formData.material,
        price: parseFloat(formData.price),
        icon: formData.icon || null
      };

      const url = editingItem 
        ? `${protocol}//${host}/api/shop/${editingItem.id}`
        : `${protocol}//${host}/api/shop`;
      
      const method = editingItem ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(item)
      });

      const data = await response.json();

      if (data.success) {
        setSuccess(editingItem ? 'Item updated!' : 'Item added!');
        setFormData({ name: '', material: '', price: '', icon: '' });
        setEditingItem(null);
        loadShopItems();
        setTimeout(() => setSuccess(''), 3000);
      } else {
        setError(data.message || 'Failed to save item');
      }
    } catch (err) {
      setError('Failed to save item');
    } finally {
      setLoading(false);
    }
  };

  const deleteItem = async (id: string) => {
    if (!confirm('Delete this item?')) return;

    setLoading(true);
    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const response = await fetch(`${protocol}//${host}/api/shop/${id}`, {
        method: 'DELETE'
      });

      const data = await response.json();

      if (data.success) {
        setSuccess('Item deleted!');
        loadShopItems();
        setTimeout(() => setSuccess(''), 3000);
      }
    } catch (err) {
      setError('Failed to delete item');
    } finally {
      setLoading(false);
    }
  };

  const startEdit = (item: ShopItem) => {
    setEditingItem(item);
    setFormData({
      name: item.name,
      material: item.material,
      price: item.price.toString(),
      icon: item.icon || ''
    });
  };

  const cancelEdit = () => {
    setEditingItem(null);
    setFormData({ name: '', material: '', price: '', icon: '' });
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <ShoppingCart className="h-5 w-5 text-blue-500" />
            Shop Configuration
          </CardTitle>
          <CardDescription>Manage shop items, prices, and icons</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Messages */}
          {error && (
            <div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg text-destructive">
              {error}
            </div>
          )}
          {success && (
            <div className="p-4 bg-green-500/10 border border-green-500/20 rounded-lg text-green-500">
              {success}
            </div>
          )}

          {/* Add/Edit Form */}
          <form onSubmit={handleSubmit} className="space-y-4 p-6 bg-gradient-to-br from-blue-500/10 to-purple-500/10 border border-blue-500/20 rounded-lg">
            <h3 className="font-semibold flex items-center gap-2">
              {editingItem ? <Edit className="h-4 w-4" /> : <Plus className="h-4 w-4" />}
              {editingItem ? 'Edit Item' : 'Add New Item'}
            </h3>
            
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="name">Item Name</Label>
                <Input
                  id="name"
                  placeholder="Diamond Sword"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="material">Material (Minecraft ID)</Label>
                <Input
                  id="material"
                  placeholder="DIAMOND_SWORD"
                  value={formData.material}
                  onChange={(e) => setFormData({ ...formData, material: e.target.value })}
                  required
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="price">Price (coins)</Label>
                <Input
                  id="price"
                  type="number"
                  step="0.01"
                  placeholder="100"
                  value={formData.price}
                  onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                  required
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="icon">Icon URL (optional)</Label>
                <Input
                  id="icon"
                  type="url"
                  placeholder="https://..."
                  value={formData.icon}
                  onChange={(e) => setFormData({ ...formData, icon: e.target.value })}
                />
              </div>
            </div>

            <div className="flex gap-2">
              <Button type="submit" disabled={loading}>
                {editingItem ? <Edit className="h-4 w-4 mr-2" /> : <Plus className="h-4 w-4 mr-2" />}
                {editingItem ? 'Update Item' : 'Add Item'}
              </Button>
              {editingItem && (
                <Button type="button" variant="outline" onClick={cancelEdit}>
                  Cancel
                </Button>
              )}
            </div>
          </form>

          {/* Items List */}
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="font-semibold flex items-center gap-2">
                <Package className="h-5 w-5 text-green-500" />
                Shop Items ({items.length})
              </h3>
              <Button variant="outline" size="sm" onClick={loadShopItems} disabled={loading}>
                Refresh
              </Button>
            </div>

            {items.length === 0 ? (
              <div className="text-center py-12 text-muted-foreground">
                No items in shop. Add your first item!
              </div>
            ) : (
              <div className="grid gap-4 md:grid-cols-2">
                {items.map((item) => (
                  <div
                    key={item.id}
                    className="p-4 bg-gradient-to-br from-card to-card/50 border rounded-lg hover:border-primary/50 transition-all duration-200"
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <h4 className="font-semibold text-lg">{item.name}</h4>
                        <p className="text-sm text-muted-foreground mt-1">
                          <span className="inline-flex items-center gap-1">
                            <Package className="h-3 w-3" />
                            {item.material}
                          </span>
                        </p>
                        <p className="text-lg font-bold text-yellow-500 mt-2 flex items-center gap-1">
                          <DollarSign className="h-4 w-4" />
                          {item.price.toFixed(2)} coins
                        </p>
                        {item.icon && (
                          <a href={item.icon} target="_blank" rel="noopener noreferrer" 
                             className="text-xs text-blue-500 hover:underline mt-1 inline-block">
                            View Icon
                          </a>
                        )}
                      </div>
                      <div className="flex gap-2">
                        <Button 
                          variant="outline" 
                          size="icon"
                          onClick={() => startEdit(item)}
                        >
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button 
                          variant="destructive" 
                          size="icon"
                          onClick={() => deleteItem(item.id)}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </CardContent>
        <BorderTrail
          className="bg-gradient-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700"
          size={120}
        />
      </Card>
    </div>
  );
}
