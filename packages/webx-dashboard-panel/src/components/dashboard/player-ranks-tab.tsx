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
import { CheckCircle, XCircle, Clock, Trash2 } from "lucide-react";

interface PlayerRank {
  playerId: string;
  playerName: string;
  primaryRank: string;
  additionalRanks: string[];
  assignedAt: number;
  assignedBy: string;
  reason: string;
  expiresAt: number;
  active: boolean;
}

interface RankOption {
  id: string;
  displayName: string;
}

export function PlayerRanksTab() {
  const [playerRanks, setPlayerRanks] = useState<PlayerRank[]>([]);
  const [ranks, setRanks] = useState<RankOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedPlayer, setSelectedPlayer] = useState<PlayerRank | null>(null);
  const [showAssignDialog, setShowAssignDialog] = useState(false);
  const [formData, setFormData] = useState({
    playerUUID: "",
    rankId: "",
    assignedBy: "",
    reason: "",
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [ranksRes, statsRes] = await Promise.all([
        fetch("/api/v1/ranks"),
        fetch("/api/v1/ranks/statistics/distribution"),
      ]);

      const ranksData = await ranksRes.json();
      const statsData = await statsRes.json();

      if (ranksData.success) {
        setRanks(
          ranksData.data.map((r: any) => ({
            id: r.id,
            displayName: r.displayName,
          }))
        );
      }

      if (statsData.success) {
        // Stats data received - you can process it here
      }
    } catch (error) {
      console.error("Failed to fetch data:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleAssignRank = async () => {
    if (!formData.playerUUID || !formData.rankId) {
      alert("Please fill in all required fields");
      return;
    }

    try {
      const response = await fetch(
        `/api/v1/ranks/players/${formData.playerUUID}/assign`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            rankId: formData.rankId,
            assignedBy: formData.assignedBy || "ADMIN",
            reason: formData.reason || "Admin assigned",
          }),
        }
      );

      if (response.ok) {
        fetchData();
        setShowAssignDialog(false);
        setFormData({
          playerUUID: "",
          rankId: "",
          assignedBy: "",
          reason: "",
        });
        alert("Rank assigned successfully!");
      }
    } catch (error) {
      console.error("Failed to assign rank:", error);
      alert("Failed to assign rank");
    }
  };

  const handleRemoveRank = async (playerUUID: string) => {
    if (!confirm("Remove rank from this player?")) return;

    try {
      const response = await fetch(
        `/api/v1/ranks/players/${playerUUID}/remove`,
        {
          method: "POST",
        }
      );

      if (response.ok) {
        fetchData();
      }
    } catch (error) {
      console.error("Failed to remove rank:", error);
    }
  };

  const formatDate = (timestamp: number) => {
    if (!timestamp) return "Never";
    return new Date(timestamp).toLocaleDateString();
  };

  const formatTime = (timestamp: number) => {
    if (!timestamp) return "N/A";
    const diff = timestamp - Date.now();
    if (diff < 0) return "Expired";
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    return `${days}d ${hours}h`;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-muted-foreground">Loading player ranks...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Player Rank Assignment */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div>
            <CardTitle>Player Rank Management</CardTitle>
            <CardDescription>
              Assign and manage ranks for individual players
            </CardDescription>
          </div>
          <Dialog open={showAssignDialog} onOpenChange={setShowAssignDialog}>
            <DialogTrigger asChild>
              <Button>Assign Rank</Button>
            </DialogTrigger>
            <DialogContent className="max-w-md">
              <DialogHeader>
                <DialogTitle>Assign Rank to Player</DialogTitle>
                <DialogDescription>
                  Select a player and rank to assign
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="player-uuid">Player UUID</Label>
                  <Input
                    id="player-uuid"
                    value={formData.playerUUID}
                    onChange={(e) =>
                      setFormData({ ...formData, playerUUID: e.target.value })
                    }
                    placeholder="12345678-1234-1234-1234-123456789012"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="rank-select">Rank</Label>
                  <Select
                    value={formData.rankId}
                    onValueChange={(value) =>
                      setFormData({ ...formData, rankId: value })
                    }
                  >
                    <SelectTrigger id="rank-select">
                      <SelectValue placeholder="Select a rank" />
                    </SelectTrigger>
                    <SelectContent>
                      {ranks.map((rank) => (
                        <SelectItem key={rank.id} value={rank.id}>
                          {rank.displayName}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="assigned-by">Assigned By</Label>
                  <Input
                    id="assigned-by"
                    value={formData.assignedBy}
                    onChange={(e) =>
                      setFormData({ ...formData, assignedBy: e.target.value })
                    }
                    placeholder="Admin name"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="reason">Reason</Label>
                  <Input
                    id="reason"
                    value={formData.reason}
                    onChange={(e) =>
                      setFormData({ ...formData, reason: e.target.value })
                    }
                    placeholder="Assignment reason"
                  />
                </div>

                <Button onClick={handleAssignRank} className="w-full">
                  Assign Rank
                </Button>
              </div>
            </DialogContent>
          </Dialog>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <Input
              placeholder="Search by player name..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />

            <div className="border rounded-lg overflow-hidden">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Player</TableHead>
                    <TableHead>Rank</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Assigned By</TableHead>
                    <TableHead>Expires</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {playerRanks && playerRanks.length > 0 ? (
                    playerRanks
                      .filter((pr) =>
                        pr.playerName
                          .toLowerCase()
                          .includes(searchTerm.toLowerCase())
                      )
                      .map((playerRank) => (
                        <TableRow key={playerRank.playerId}>
                          <TableCell className="font-medium">
                            {playerRank.playerName}
                          </TableCell>
                          <TableCell>
                            <Badge>{playerRank.primaryRank}</Badge>
                          </TableCell>
                          <TableCell>
                            {playerRank.active ? (
                              <div className="flex items-center gap-2">
                                <CheckCircle className="h-4 w-4 text-green-500" />
                                <span className="text-sm">Active</span>
                              </div>
                            ) : (
                              <div className="flex items-center gap-2">
                                <XCircle className="h-4 w-4 text-red-500" />
                                <span className="text-sm">Inactive</span>
                              </div>
                            )}
                          </TableCell>
                          <TableCell className="text-sm text-muted-foreground">
                            {playerRank.assignedBy}
                          </TableCell>
                          <TableCell>
                            {playerRank.expiresAt > 0 ? (
                              <div className="flex items-center gap-2">
                                <Clock className="h-4 w-4" />
                                <span className="text-sm">
                                  {formatTime(playerRank.expiresAt)}
                                </span>
                              </div>
                            ) : (
                              <span className="text-sm text-muted-foreground">
                                Permanent
                              </span>
                            )}
                          </TableCell>
                          <TableCell className="text-right">
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() =>
                                handleRemoveRank(playerRank.playerId)
                              }
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={6} className="text-center py-8">
                        <p className="text-muted-foreground">
                          No player ranks found. Assign a rank to get started.
                        </p>
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
