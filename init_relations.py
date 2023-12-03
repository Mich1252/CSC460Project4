import os

USERNAME = "jhihyangwu"
PASSWORD = "a4608"

def main():
    print("Make sure you ran 'set autocommit off' on 'sqlpl username@oracle.aloe'")
    print("Make sure you ran 'export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}' on command line")
    print("This script can be ran multiple times, just remember to enter Y if the tables need to be dropped first")
    print()
    all_statements = []
    # If the tables are already created, delete them first.
    already_created = None
    while already_created not in ["Y", "N"]:
        already_created = input("Are the tables already created on oracle.aloe? (Y/N) ")
    if already_created == "Y":
        all_statements += deleteAllTables()
    # Create tables.
    all_statements += createTransactionTable()
    all_statements += createEquipmentTable()
    all_statements += createBorrowedTable()
    all_statements += createMemberTable()
    all_statements += createMemberEnrolledTable()
    all_statements += createPackageTable()
    all_statements += createPackageCoursesTable()
    all_statements += createMembershipsTable()
    all_statements += createMembershipInfoTable()
    all_statements += createTrainerTable()
    all_statements += createCourseTable()

    # Fill tables.
    all_statements += fillTransactionTable()
    all_statements += fillEquipmentTable()
    all_statements += fillBorrowedTable()
    all_statements += fillMemberTable()
    all_statements += fillMemberEnrolledTable()
    all_statements += fillPackageTable()
    all_statements += fillPackageCoursesTable()
    all_statements += fillMembershipsTable()
    all_statements += fillMembershipInfoTable()
    all_statements += fillTrainerTable()
    all_statements += fillCourseTable()
    # Actually run the Oracle SQL statements.
    with open("temp1528.txt", "w") as file:
        for line in all_statements:
            file.write(line.replace("\n", "") + "\n")
    os.system(f"java InitRelations.java {USERNAME} {PASSWORD}")

def deleteAllTables():
    retval = []
    retval.append("drop table Transaction")
    retval.append("drop table Equipment")
    retval.append("drop table Borrowed")
    retval.append("drop table Member")
    retval.append("drop table Package")
    retval.append("drop table Memberships")
    retval.append("drop table Trainer")
    retval.append("drop table Course")
    retval.append("commit")
    return retval

def createTransactionTable():
    retval = []
    retval.append("""create table Transaction (
        TransactionID integer,
        MemberID integer,
        Amount float,
        TransactionDate Date,
        TransactionType varchar2(20),
        Paid char(1),
        primary key (TransactionID, MemberID)
    )""")
    retval.append("commit")
    return retval

def createEquipmentTable():
    retval = []
    retval.append("""create table Equipment (
        EquipmentID integer,
        EquipmentName varchar2(20),
        Available integer,
        MaxQuantity integer,
        primary key (EquipmentID, EquipmentName)
    )""")
    retval.append("commit")
    return retval

def createBorrowedTable():
    retval = []
    retval.append("""create table Borrowed (
        MemberID integer,
        EquipmentID integer,
        QuantityBorrowed integer,
        CheckOutTime Date,
        ReturnTime Date,
        isLost char(1),
        primary key (MemberID, EquipmentID, CheckOutTime)
    )""")
    retval.append("commit")
    return retval

def createMemberTable():
    retval = []
    retval.append("""create table Member (
        MemberID integer,
        Name varchar2(50),
        PhoneNum char(12),
        AccountBalance float,
        MembershipID integer,
        AmountSpent float,
        primary key (MemberID, PhoneNum)
    )""")
    retval.append("commit")
    return retval

def createMemberEnrolledTable():
    retval = []
    retval.append("""create table MemberEnrolled (
        MemberID integer,
        PackageID Integer,
        primary key (MemberID, PackageID)
    )""")
    retval.append("commit")
    return retval

def createPackageTable():
    retval = []
    retval.append("""create table Package (
        PackageID integer,
        Name varchar2(50),
        Price float,
        primary key (PackageID, Name)
    )""")
    retval.append("commit")
    return retval

def createPackageCoursesTable():
    retval = []
    retval.append("""create table PackageCourses (
        PackageID integer,
        CourseID Integer,
        primary key (PackageID, CourseID)
    )""")
    retval.append("commit")
    return retval

def createMembershipsTable():
    retval = []
    retval.append("""create table Memberships (
        MembershipID integer,
        MemberID integer,
        primary key (MembershipID, MemberID)
    )""")
    retval.append("commit")
    return retval

def createMembershipInfoTable():
    retval = []
    retval.append("""create table Memberships (
        MembershipID integer,
        Name varchar2(50),
        MinSpend float,
        Discount integer,
        primary key (MembershipID, Name)
    )""")
    retval.append("commit")
    return retval

def createTrainerTable():
    retval = []
    retval.append("""create table Trainer (
        TrainerID integer,
        Name varchar2(50),
        PhoneNum char(12),
        primary key (TrainerID, PhoneNum)
    )""")
    retval.append("commit")
    return retval

def createCourseTable():
    retval = []
    retval.append("""create table Course (
        CourseID integer,
        Name varchar2(20),
        TrainerID integer,
        StartTime integer,
        StartDate Date,
        EndDate Date,
        Duration integer,
        MaxEnrolled integer,
        CurrentEnrolled integer,
        DaysOfTheWeek varchar2(14)
        primary key (CourseID, Name)
    )""")
    retval.append("commit")
    return retval

def fillTransactionTable():
    retval = []
    retval.append("delete from Transaction")  # delete all tuples
    retval.append("insert into Transaction values (0, 300, TO_DATE('11/30/2023', 'MM/DD/YYYY'), 'Recharge', 'N')")
    retval.append("insert into Transaction values (1, 50.75, TO_DATE('11/29/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (1, 25.25, TO_DATE('12/01/2023', 'MM/DD/YYYY'), 'Recharge', 'N')")
    retval.append("insert into Transaction values (2, 40.0, TO_DATE('11/28/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (3, 20.25, TO_DATE('11/30/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (3, 15.5, TO_DATE('12/02/2023', 'MM/DD/YYYY'), 'Recharge', 'Y')")
    retval.append("insert into Transaction values (4, 5.0, TO_DATE('11/29/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (4, 10.0, TO_DATE('11/30/2023', 'MM/DD/YYYY'), 'Recharge', 'N')")
    retval.append("insert into Transaction values (5, 30.75, TO_DATE('12/01/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (6, 60.0, TO_DATE('11/28/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (6, 45.25, TO_DATE('12/01/2023', 'MM/DD/YYYY'), 'Recharge', 'Y')")
    retval.append("insert into Transaction values (7, 80.25, TO_DATE('11/30/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (8, 35.5, TO_DATE('12/01/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (8, 40.25, TO_DATE('12/02/2023', 'MM/DD/YYYY'), 'Recharge', 'N')")
    retval.append("insert into Transaction values (9, 25.0, TO_DATE('11/29/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("insert into Transaction values (9, 30.0, TO_DATE('12/01/2023', 'MM/DD/YYYY'), 'Recharge', 'Y')")
    retval.append("insert into Transaction values (10, 90.75, TO_DATE('11/28/2023', 'MM/DD/YYYY'), 'Purchase', 'Y')")
    retval.append("commit")
    return retval

def fillEquipmentTable():
    retval = []
    retval.append("delete from Equipment")  # delete all tuples
    retval.append("insert into Equipment values (0, 'Basketball', 30, 31)")
    retval.append("insert into Equipment values (1, 'Tennis Racket', 20, 20)")
    retval.append("insert into Equipment values (2, 'Soccer Ball', 30, 30)")
    retval.append("insert into Equipment values (3, 'Yoga Mat', 15, 15)")
    retval.append("insert into Equipment values (4, 'Dumbbells', 20, 20)")
    retval.append("insert into Equipment values (5, 'Jump Rope', 25, 25)")
    retval.append("insert into Equipment values (6, 'Swimming Goggles', 10, 10)")
    retval.append("insert into Equipment values (7, 'Badminton Shuttlecocks', 20, 20)")
    retval.append("insert into Equipment values (8, 'Baseball', 8, 8)")
    retval.append("insert into Equipment values (9, 'Golf Clubs', 12, 12)")
    retval.append("insert into Equipment values (10, 'Baseball Bats', 18, 18)")
    retval.append("commit")
    return retval

def fillBorrowedTable():
    retval = []
    retval.append("delete from Borrowed")  # delete all tuples
    retval.append("insert into Borrowed values (0, 0, 1, TO_DATE('11/29/2023', 'MM/DD/YYYY'), '', 'N')")
    retval.append("commit")
    return retval

def fillMemberTable():
    retval = []
    retval.append("delete from Member")  # delete all tuples
    retval.append("insert into Member values (0, 'JhihYang', '000-000-0000', 1234.5, 0, 20)")
    retval.append("insert into Member values (1, 'John Doe', '111-222-3333', 1500.75, 1, 50.25)")
    retval.append("insert into Member values (2, 'Jane Smith', '555-666-7777', 2000.0, 1, 75.5)")
    retval.append("insert into Member values (3, 'Bob Johnson', '999-888-7777', 800.25, 2, 30.75)")
    retval.append("insert into Member values (4, 'Alice Brown', '444-333-2222', 300.5, 2, 10.0)")
    retval.append("insert into Member values (5, 'Charlie Wilson', '777-888-9999', 1200.0, 0, 45.75)")
    retval.append("insert into Member values (6, 'Eva Rodriguez', '555-111-2222', 1800.0, 1, 60.0)")
    retval.append("insert into Member values (7, 'Michael Chen', '777-555-4444', 2500.5, 0, 80.25)")
    retval.append("insert into Member values (8, 'Olivia Davis', '999-333-2222', 1200.75, 2, 35.5)")
    retval.append("insert into Member values (9, 'Daniel Lee', '111-444-6666', 600.25, 1, 25.0)")
    retval.append("insert into Member values (10, 'Sophia Nguyen', '888-222-3333', 3000.0, 0, 90.75)")
    retval.append("commit")
    return retval

def fillMemberEnrolledTable():
    retval = []
    retval.append("delete from MemberEnrolled")  # delete all tuples
    retval.append("insert into MemberEnrolled values (0, 0)")
    retval.append("commit")
    return retval

def fillPackageTable():
    retval = []
    retval.append("delete from Package")  # delete all tuples
    retval.append("insert into Package values (0, 'Junior Package', 450.0)")
    retval.append("insert into Package values (1, 'Senior Package', 550.0)")
    retval.append("insert into Package values (2, 'Strength Package', 550.0)")
    retval.append("commit")
    return retval

def fillMembershipsTable():
    retval = []
    retval.append("delete from Memberships")  # delete all tuples
    retval.append("insert into Memberships values (0, 'Basic', 0, 0)")
    retval.append("insert into Memberships values (1, 'Diamond', 500, 10)")
    retval.append("insert into Memberships values (2, 'Gold', 1000, 20)")
    retval.append("commit")
    return retval

def fillTrainerTable():
    retval = []
    retval.append("delete from Trainer")  # delete all tuples
    retval.append("insert into Trainer values (0, 'Dr. McCann', '111-111-1111')")
    retval.append("insert into Trainer values (1, 'Prof. Proebsting', '222-222-2222')")
    retval.append("commit")
    return retval

def fillCourseTable():
    retval = []
    retval.append("delete from Course")  # delete all tuples
    retval.append("insert into Course values (1, 'Strength 101', 0, 1400, TO_DATE('08/23/2023', 'MM/DD/YYYY'), TO_DATE('12/02/2023', 'MM/DD/YYYY'), 60, 50, 1)")
    retval.append("insert into Course values (2, 'Strength 102', 0, 1400, TO_DATE('08/23/2023', 'MM/DD/YYYY'), TO_DATE('12/02/2023', 'MM/DD/YYYY'), 60, 50, 1)")
    retval.append("insert into Course values (3, 'Yoga 101', 1, 1400, TO_DATE('08/23/2023', 'MM/DD/YYYY'), TO_DATE('12/02/2023', 'MM/DD/YYYY'), 60, 50, 1)")
    retval.append("insert into Course values (4, 'Yoga 102', 1, 1400, TO_DATE('08/23/2023', 'MM/DD/YYYY'), TO_DATE('12/02/2023', 'MM/DD/YYYY'), 60, 50, 1)")
    retval.append("commit")
    return retval

def fillPackageCoursesTable():
    retval = []
    retval.append("delete from PackageCourses")  # delete all tuples
    retval.append("insert into PackageCourses values (0, 1)")
    retval.append("insert into PackageCourses values (0, 3)")
    retval.append("insert into PackageCourses values (1, 2)")
    retval.append("insert into PackageCourses values (1, 4)")
    retval.append("insert into PackageCourses values (2, 1)")
    retval.append("insert into PackageCourses values (2, 2)")
    retval.append("commit")
    return retval

def fillMembershipInfoTable():
    retval = []
    retval.append("delete from MembershipInfo")  # delete all tuples
    retval.append("commit")
    return retval

if __name__ == "__main__":
    main()
