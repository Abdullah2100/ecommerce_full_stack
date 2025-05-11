using Microsoft.EntityFrameworkCore.Migrations;
using NetTopologySuite.Geometries;

#nullable disable

namespace ecommerc_dotnet.Migrations
{
    /// <inheritdoc />
    public partial class updatelocationonaddresstabletolongitduelatudidecolumns : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "location",
                table: "Address");

            migrationBuilder.AddColumn<decimal>(
                name: "latitude",
                table: "Address",
                type: "numeric",
                nullable: false,
                defaultValue: 0m);

            migrationBuilder.AddColumn<decimal>(
                name: "longitude",
                table: "Address",
                type: "numeric",
                nullable: false,
                defaultValue: 0m);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "latitude",
                table: "Address");

            migrationBuilder.DropColumn(
                name: "longitude",
                table: "Address");

            migrationBuilder.AddColumn<Geometry>(
                name: "location",
                table: "Address",
                type: "GEOMETRY(Point, 4326)",
                nullable: false);
        }
    }
}
