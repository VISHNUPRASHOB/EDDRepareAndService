/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Helper.DataTransferHelper;
import Models.SparePart;
import java.util.List;

/**
 *
 * @author vishnuprashob
 */
public interface ISpairPartsCSVFileManager {

    public void initializeFile();

    public DataTransferHelper<Boolean, String> saveSparePart(SparePart newPart);

    public List<SparePart> loadAllSpareParts();

    public DataTransferHelper<Boolean, String> deleteSparePart(String partId);

    public DataTransferHelper<Boolean, String> updateSparePart(SparePart updatedPart);

    public SparePart getSparePartById(String partId);
}
