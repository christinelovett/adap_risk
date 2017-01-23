Select * from Recordtype
Select * from Category
Select * from Pathway_Category
Select * from Scenario
Select * from Pathway
Select * from ScenarioPathwaymbr
Select * from PathwayPathwaymbr
Select * from pathway_pathway_category_left_outer

REM Scenario and related Pathways
Select s.name, p.name from Scenario s, ScenarioPathwaymbr spm, pathway p, pathwaypathwaymbr ppm
where s.id = spm.scenario_id 
and spm.pathway_id = p.id

REM Pathway and related Pathways  (ALL INNER JOINS)
Select 
 p1.name as Pathway_Parent, p1.id, r1.name as Pathway_Parent_Recordtype, ppm.logicoperator,
 p2.name as Pathway_child, p2.id, r2.name as Child_Parent_Recordtype from pathway p1,pathway p2, 
 pathwaypathwaymbr ppm, 
 recordtype r1, recordtype r2
where ppm.parentpathway_id = p1.id
and ppm.childpathway_id = p2.id
and p1.recordtype_id= r1.id 
and p2.recordtype_id = r2.id 


REM Retrieve Categories for a Pathway (LEFT OUTER JOIN) fro null Categories
Select distinct p.id,pc.categories_id, c.name  from 
pathway p LEFT OUTER JOIN pathway_category pc ON p.id =pc.pathways_id, 
pathway_category pc2 LEFT OUTER JOIN category c ON pc2.categories_id  = c.id
order by p.id 




