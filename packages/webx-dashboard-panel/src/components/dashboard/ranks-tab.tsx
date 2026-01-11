"use client";

import React, { useState, useEffect } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Plus, Edit2, Trash2, ChevronDown } from "lucide-react";

interface Rank {
  id: string;
  displayName: string;
  priority: number;
  prefix: string;
  suffix: string;
  purchasable: boolean;
  purchasePrice: number;
  assignable: boolean;
  permissions: string[];
  features: Record<string, boolean>;
  createdAt: number;
  updatedAt: number;
}

export function RanksTab() {
  const [ranks, setRanks] = useState<Rank[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingRank, setEditingRank] = useState<Rank | null>(null);
  const [showNewRankDialog, setShowNewRankDialog] = useState(false);
  const [formData, setFormData] = useState({
    id: "",
    displayName: "",
    priority: 1,
    prefix: "",
    suffix: "",
    purchasable: false,
    purchasePrice: 0,
    assignable: true,
  });

  useEffect(() => {
    fetchRanks();
  }, []);

  const fetchRanks = async () => {
    try {
      setLoading(true);
      const response = await fetch("/api/v1/ranks");
      const result = await response.json();
      
      if (result.success) {
        setRanks(result.data || []);
      }
    } catch (error) {
      console.error("Failed to fetch ranks:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveRank = async () => {
    try {
      if (editingRank) {
        const response = await fetch(`/api/v1/ranks/${editingRank.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        });

        if (response.ok) {
          fetchRanks();
          setEditingRank(null);
        }
      } else {
        const response = await fetch("/api/v1/ranks", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        });

        if (response.ok) {
          fetchRanks();
          setShowNewRankDialog(false);
          setFormData({
            id: "",
            displayName: "",
            priority: 1,
            prefix: "",
            suffix: "",
            purchasable: false,
            purchasePrice: 0,
            assignable: true,
          });
        }
      }
    } catch (error) {
      console.error("Failed to save rank:", error);
    }
  };

  const handleDeleteRank = async (rankId: string) => {
    if (!confirm(`Delete rank ${rankId}?`)) return;

    try {
      const response = await fetch(`/api/v1/ranks/${rankId}`, {
        method: "DELETE",
      });

      if (response.ok) {
        fetchRanks();
      }
    } catch (error) {
      console.error("Failed to delete rank:", error);
    }
  };

  const handleEditRank = (rank: Rank) => {
    setEditingRank(rank);
    setFormData({
      id: rank.id,
      displayName: rank.displayName,
      priority: rank.priority,
      prefix: rank.prefix,
      suffix: rank.suffix,
      purchasable: rank.purchasable,
      purchasePrice: rank.purchasePrice,
      assignable: rank.assignable,
    });
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-muted-foreground">Loading ranks...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Rank Management Card */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div>
            <CardTitle>Ranks Management</CardTitle>
            <CardDescription>
              Create and manage server ranks and permissions
            </CardDescription>
          </div>
          <Dialog open={showNewRankDialog} onOpenChange={setShowNewRankDialog}>
            <DialogTrigger asChild>
              <Button className="gap-2">
                <Plus className="h-4 w-4" />
                New Rank
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-md">
              <DialogHeader>
                <DialogTitle>Create New Rank</DialogTitle>
                <DialogDescription>
                  Add a new rank to your server
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="rank-id">Rank ID</Label>
                  <Input
                    id="rank-id"
                    value={formData.id}
                    onChange={(e) =>
                      setFormData({ ...formData, id: e.target.value })
                    }
                    placeholder="vip, premium, etc."
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="display-name">Display Name</Label>
                  <Input
                    id="display-name"
                    value={formData.displayName}
                    onChange={(e) =>
                      setFormData({ ...formData, displayName: e.target.value })
                    }
                    placeholder="VIP, Premium Member, etc."
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="priority">Priority</Label>
                  <Input
                    id="priority"
                    type="number"
                    value={formData.priority}
                    onChange={(e) =>
                      setFormData({ ...formData, priority: parseInt(e.target.value) })
                    }
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="prefix">Prefix (use & for color codes)</Label>
                  <Input
                    id="prefix"
                    value={formData.prefix}
                    onChange={(e) =>
                      setFormData({ ...formData, prefix: e.target.value })
                    }
                    placeholder="&b[VIP]&r"
                  />
                </div>
                <Button onClick={handleSaveRank} className="w-full">
                  Create Rank
                </Button>
              </div>
            </DialogContent>
          </Dialog>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Rank ID</TableHead>
                <TableHead>Display Name</TableHead>
                <TableHead>Priority</TableHead>
                <TableHead>Prefix</TableHead>
                <TableHead>Purchasable</TableHead>
                <TableHead>Price</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {ranks.map((rank) => (
                <TableRow key={rank.id}>
                  <TableCell className="font-medium">{rank.id}</TableCell>
                  <TableCell>{rank.displayName}</TableCell>
                  <TableCell>
                    <Badge variant="outline">{rank.priority}</Badge>
                  </TableCell>
                  <TableCell>
                    <code className="text-sm">{rank.prefix}</code>
                  </TableCell>
                  <TableCell>
                    <Badge variant={rank.purchasable ? "default" : "secondary"}>
                      {rank.purchasable ? "Yes" : "No"}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    {rank.purchasable ? (
                      <code>{rank.purchasePrice} coins</code>
                    ) : (
                      "-"
                    )}
                  </TableCell>
                  <TableCell className="text-right space-x-2">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleEditRank(rank)}
                    >
                      <Edit2 className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleDeleteRank(rank.id)}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Edit Rank Dialog */}
      {editingRank && (
        <Dialog open={!!editingRank} onOpenChange={() => setEditingRank(null)}>
          <DialogContent className="max-w-2xl max-h-96 overflow-y-auto">
            <DialogHeader>
              <DialogTitle>Edit Rank: {editingRank.displayName}</DialogTitle>
              <DialogDescription>
                Modify rank properties and permissions
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Display Name</Label>
                  <Input
                    value={formData.displayName}
                    onChange={(e) =>
                      setFormData({ ...formData, displayName: e.target.value })
                    }
                  />
                </div>
                <div className="space-y-2">
                  <Label>Priority</Label>
                  <Input
                    type="number"
                    value={formData.priority}
                    onChange={(e) =>
                      setFormData({ ...formData, priority: parseInt(e.target.value) })
                    }
                  />
                </div>
                <div className="space-y-2">
                  <Label>Prefix</Label>
                  <Input
                    value={formData.prefix}
                    onChange={(e) =>
                      setFormData({ ...formData, prefix: e.target.value })
                    }
                  />
                </div>
                <div className="space-y-2">
                  <Label>Suffix</Label>
                  <Input
                    value={formData.suffix}
                    onChange={(e) =>
                      setFormData({ ...formData, suffix: e.target.value })
                    }
                  />
                </div>
              </div>

              <div className="space-y-3 border-t pt-4">
                <h3 className="font-semibold">Purchase Settings</h3>
                <div className="flex items-center gap-4">
                  <div className="flex-1 space-y-2">
                    <Label>Purchasable</Label>
                    <Select
                      value={formData.purchasable ? "yes" : "no"}
                      onValueChange={(value) =>
                        setFormData({ ...formData, purchasable: value === "yes" })
                      }
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="yes">Yes</SelectItem>
                        <SelectItem value="no">No</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  {formData.purchasable && (
                    <div className="flex-1 space-y-2">
                      <Label>Price (coins)</Label>
                      <Input
                        type="number"
                        value={formData.purchasePrice}
                        onChange={(e) =>
                          setFormData({
                            ...formData,
                            purchasePrice: parseInt(e.target.value),
                          })
                        }
                      />
                    </div>
                  )}
                </div>
              </div>

              <div className="space-y-3">
                <h3 className="font-semibold">Permissions</h3>
                <div className="bg-muted p-3 rounded-md text-sm">
                  <p className="text-muted-foreground">
                    Total Permissions: {editingRank.permissions.length}
                  </p>
                  <ul className="list-disc list-inside mt-2 space-y-1">
                    {editingRank.permissions.slice(0, 5).map((perm) => (
                      <li key={perm} className="text-xs text-muted-foreground">
                        {perm}
                      </li>
                    ))}
                    {editingRank.permissions.length > 5 && (
                      <li className="text-xs text-muted-foreground">
                        +{editingRank.permissions.length - 5} more
                      </li>
                    )}
                  </ul>
                </div>
              </div>

              <Button onClick={handleSaveRank} className="w-full">
                Save Changes
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      )}
    </div>
  );
}
