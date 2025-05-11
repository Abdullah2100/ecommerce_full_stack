using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ecommerc_dotnet.Migrations
{
    /// <inheritdoc />
    public partial class updateaddresscolumnaddingtitleandtimescolumn : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<DateTime>(
                name: "created_at",
                table: "Address",
                type: "Timestamp",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<bool>(
                name: "isCurrent",
                table: "Address",
                type: "boolean",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<string>(
                name: "title",
                table: "Address",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<DateTime>(
                name: "updated_at",
                table: "Address",
                type: "Timestamp",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "created_at",
                table: "Address");

            migrationBuilder.DropColumn(
                name: "isCurrent",
                table: "Address");

            migrationBuilder.DropColumn(
                name: "title",
                table: "Address");

            migrationBuilder.DropColumn(
                name: "updated_at",
                table: "Address");
        }
    }
}
